package com.google.servlets;

import com.google.gson.Gson; 
import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/get-user-lists")
public class GetUserListsServlet extends HttpServlet {
  private class RequestBody {
    int userId;
  }

  private class ResponseBody {
    List<List<String>> userLists;

    public ResponseBody(List<List<String>> userLists) {
      this.userLists = userLists;
    }
  }

  /*
   * This handles a POST request to "/get-user-lists". The user's saved lists 
   * are returned. 
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    String reqString = ServletUtil.getRequestBody(request);
    RequestBody requestBody = requestValidator(reqString);
    if (requestBody == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    List<List<String>> userLists = LibraryFunctions.getUserLists(requestBody.userId);
    if (userLists == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        "An error occured while retriving data from the database.");
      return;
    }

    ResponseBody responseBody = new ResponseBody(userLists);
    response.setContentType("application/json;");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(g.toJson(responseBody));
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.userId == 0) {
      return null;
    }
    return requestBody;
  }
}