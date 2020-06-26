package com.google.servlets;

import com.google.gson.Gson;
import java.io.BufferedReader;
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
  private class RequestBody {
    private int userId;
    private UserList userList;
  }

  private class UserList {
    private int listId;
    private String displayName;
    private List<String> itemTypes;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    SpannerUtilFunctions suf = new SpannerUtilFunctions();
    RequestBody requestBody;
    try {
      requestBody = getRequestBody(request);
      if (requestBody == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println("");
      }
    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("");
      return;
    }
    try {
      suf.writeUserLists(
          requestBody.userId, requestBody.userList.listId, requestBody.userList.itemTypes);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().println("");
      return;
    }
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println("");
  }

  private RequestBody getRequestBody(HttpServletRequest request) throws IOException {
    Gson g = new Gson();
    BufferedReader br = request.getReader();
    String reqString = "";
    String line;

    while ((line = br.readLine()) != null) {
      reqString += line;
    }
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.userId == 0 || requestBody.userList == null
        || requestBody.userList.listId == 0 || requestBody.userList.displayName == null
        || requestBody.userList.itemTypes == null) {
      return null;
    }
    return requestBody;
  }
}