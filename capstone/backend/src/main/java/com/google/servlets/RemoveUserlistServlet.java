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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that removes a user list. */
@WebServlet("/api/v1/remove-user-list")
public class RemoveUserlistServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson g = new Gson();
    String listIdString = request.getParameter("listId");
    String userIdString = request.getParameter("userId");

    if (userIdString == null || userIdString == "" || listIdString == null || listIdString == "") {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    try {
      deleteUserlist(Long.parseLong(listIdString), Long.parseLong(userIdString));
    } catch (SpannerException se) {
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occured when trying to delete this list.");
      return;
    }

    response.setStatus(HttpServletResponse.SC_OK);
  }

  public void deleteUserlist(long listId, long userId) throws SpannerException {
    LibraryFunctions.deleteUserList(listId, userId);
  }
}
