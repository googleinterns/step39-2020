/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.servlets;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.cloud.spanner.ErrorCode;
import com.google.cloud.spanner.SpannerException;
import com.google.gson.Gson;
import com.google.spanner.LibraryFunctions;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that adds a new user to the database.
 */
@WebServlet("/api/v1/create-user")
public class CreateUserServlet extends HttpServlet {
  private GoogleIdTokenVerifier verifier =
      new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).build();

  private class RequestBody { String idTokenString; }

  private class ResponseBody {
    long userId;

    public ResponseBody(long userId) {
      this.userId = userId;
    }
  }

  /*
   * This handles a POST request to "/create-user". A new user object is created
   * and added to the Users table in the database.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    String reqString = ServletUtil.getRequestBody(request);
    RequestBody requestBody = requestValidator(reqString);
    Payload payload;

    if (requestBody == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    try {
      payload = verifyIdToken(requestBody.idTokenString);
    } catch (GeneralSecurityException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }
    long userId = payload.getSubject().hashCode();
    try {
      createUser(userId, (String) payload.get("name"), payload.getEmail());
    } catch (SpannerException se) {
      if (se.getErrorCode() != ErrorCode.ALREADY_EXISTS) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occured while adding a new user to the databse.");
      return;
      }
    }

    ResponseBody responseBody = new ResponseBody(userId);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(g.toJson(responseBody));
  }

  private Payload verifyIdToken(String idTokenString) throws GeneralSecurityException, IOException {
    GoogleIdToken idToken = verifier.verify(idTokenString);
    if (idToken != null) {
      Payload payload = idToken.getPayload();
      return payload;
    } else {
      return null;
    }
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.idTokenString == null) {
      return null;
    }
    return requestBody;
  }

  public void setVerifier(GoogleIdTokenVerifier verifier) {
    this.verifier = verifier;
  }

  public void createUser(long userId, String userName, String email) throws SpannerException {
    LibraryFunctions.createUser(userId, userName, email);
  }
}