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
    SpannerUtilFunctions suf = new SpannerUtilFunctions();
    RequestBody requestBody = getRequestBody(request);
    if (requestBody == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    List<List<String>> userLists = suf.getUserLists(userId);
    if (userLists == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        "An error occured while retriving data from the database.");
      return;
    }

    ResponseBody responseBody = new ResponseBody(ResponseStatus.SUCCESS, userLists);
    response.setContentType("application/json;");
    response.setStatus(HttpServletRequest.SC_OK);
    response.getWriter().println(g.toJson(responseBody));
  }

  private RequestBody getRequestBody(HttpServletRequest request) {
    Gson g = new Gson();
    StringBuilder sb = new StringBuilder();
    String reqString = "";
    String line;
    try {
      BufferedReader br = request.getReader();
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      return null;
    }
    reqString = sb.toString();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.userId == 0) {
      return null;
    }
    return requestBody;
  }
}