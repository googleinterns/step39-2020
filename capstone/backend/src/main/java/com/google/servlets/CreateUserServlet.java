package com.google.servlets;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that adds a new user to the database. 
 */
@WebServlet("/api/v1/create-user")
public class CreateUserServlet extends HttpServlet {
  private static final long INVALID_USER_ID = -1;

  private class RequestBody {
    String email;
    String userName;
  }

  private class ResponseBody {
    long userId;
    public ResponseBody(long userId) {
      this.userId = userId;
    }
  }

  /*
   * This handles a POST request to "/create-user". A new user object is created and added to
   * the Users table in the database.
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
    long userId = LibraryFunctions.createUser(requestBody.email.hashCode(), requestBody.userName, 
        requestBody.email);
    if (userId == INVALID_USER_ID) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occured while adding a new user to the databse.");
      return;
    }

    ResponseBody responseBody = new ResponseBody(userId);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(g.toJson(responseBody));
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.email == null || requestBody.userName == null) {
      return null;
    }
    return requestBody;
  }
}