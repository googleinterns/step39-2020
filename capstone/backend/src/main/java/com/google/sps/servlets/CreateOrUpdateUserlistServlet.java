package com.google.sps.servlets;

import com.google.gson.Gson; 
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
* Servlet that creates or updates user lists. 
*/
@WebServlet("/api/v1/create-or-update-user-list-servlet")
public class CreateOrUpdateUserlistServlet extends HttpServlet {
  private static final int OK = 200;
  private static final int BAD_REQUEST = 400;
  private static final int INTERNAL_SERVER_ERROR = 500;

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
   try {
     RequestBody requestBody = getRequestBody(request);
   } catch (IOException) {
      response.setStatus(BAD_REQUEST)
      response.getWriter().println("");
      return;
   }
   try {
     suf.writeUserLists(requestBody.userId. requestBody.UserList.listId, requestBody.UserList.itemTypes);
   } catch (Exception e) {
     response.setStatus(INTERNAL_SERVER_ERROR);
     response.getWriter().println("");
     return;
   }
   response.setStatus(OK);
   response.getWriter().println("");
 }

  private RequestBody getRequestBody(HttpServletRequest request) throws IOException {
    Gson g = new Gson();
    BufferedReader br = request.getReader();
    String reqString = "";
    String line;

    while((line = br.readLine()) != null) {
      reqString += line;
    }
    return g.fromJson(reqString, RequestBody.class);
  }
}