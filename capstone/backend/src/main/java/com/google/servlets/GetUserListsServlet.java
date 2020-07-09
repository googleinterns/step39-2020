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
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/get-user-lists")
public class GetUserListsServlet extends HttpServlet {
  private class ResponseBody {
    private List<UserList> userLists;

    public ResponseBody(List<UserList> userLists) {
      this.userLists = userLists;
    }
  }

  /*
   * This handles a GET request to "/get-user-lists". The user's saved lists
   * are returned.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    String userIdString = request.getParameter("userId");

    if (userIdString == null || userIdString == "") {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    long userId = Long.parseLong(userIdString);
    List<UserList> userLists;
    try {
      userLists = getUserLists(userId);
    } catch (SpannerException se) {
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occured while retriving data from the database.");
      return;
    }

    ResponseBody responseBody = new ResponseBody(userLists);
    response.setContentType("application/json;");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(g.toJson(responseBody));
  }

  public List<UserList> getUserLists(long userId) throws SpannerException {
    return LibraryFunctions.getUserLists(userId);
  }
}
