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
    private long userId;
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
      writeUserLists(requestBody.userId, requestBody.userList.listId,
          requestBody.userList.itemTypes, requestBody.userList.displayName);
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

  private long generateListId(long userId) {
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

  public void writeUserLists(long userId, long listId, List<String> itemTypes, String displayName)
      throws SpannerException {
    LibraryFunctions.writeUserLists(userId, listId, itemTypes, displayName);
  }
}
