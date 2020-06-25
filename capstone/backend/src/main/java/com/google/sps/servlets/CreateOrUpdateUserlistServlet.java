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
* 
*/
@WebServlet("/api/v1/create-or-update-user-list-servlet")
public class CreateOrUpdateUserlistServlet extends HttpServlet {
  private class ResponseBody {
    private ResponseStatus responseStatus;

    public ResponseBody(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
  }

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
   RequestBody requestBody = getRequestBody(request);
   DatabaseClient dbClient = initClient();
   writeData(requestBody, dbClient);

   ResponseBody responseBody = new ResponseBody(ResponseStatus.SUCCESS);
   response.setContentType("application/json;");
   response.getWriter().println(g.toJson(responseBody));
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