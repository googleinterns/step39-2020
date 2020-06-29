package com.google.servlets;

import com.google.cloud.spanner.SpannerException;
import com.google.gson.Gson;
import com.google.spanner.LibraryFunctions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that creates or updates user lists.
 */
@WebServlet("/api/v1/create-or-update-user-list-servlet")
public class CreateOrUpdateUserlistServlet extends HttpServlet {
  private static final int NEW_LIST = -1;
  private class RequestBody {
    private int userId;
    private UserList userList;
  }

  private class ResponseBody {
    private UserList userList;

    public ResponseBody(UserList userList) {
      this.userList = userList;
    }
  }

  private class UserList {
    private long listId;
    private String displayName;
    private List<String> itemTypes;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    String reqString = ServletUtil.getRequestBody(request);
    RequestBody requestBody = requestValidator(reqString);

    if (requestBody == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    if (requestBody.userList.listId == NEW_LIST) {
      requestBody.userList.listId = generateListId(requestBody.userId);
    }
    try {
      LibraryFunctions.writeUserLists(
            requestBody.userId, requestBody.userList.listId, requestBody.userList.itemTypes);
    } catch (SpannerException se) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occured while writing to the database.");
      return;
    }
  
    ResponseBody responseBody = new ResponseBody(requestBody.userList);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(g.toJson(responseBody));
  }

  private long generateListId(int userId) {
    long currentTime = System.currentTimeMillis();
    String id = String.valueOf(userId) + currentTime;
    return id.hashCode();
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.userId == 0 || requestBody.userList == null
        || requestBody.userList.listId == 0 || requestBody.userList.displayName == null
        || requestBody.userList.itemTypes == null) {
      return null;
    }
    return requestBody;
  }
}
