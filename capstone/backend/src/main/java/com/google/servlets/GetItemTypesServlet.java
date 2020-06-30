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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 @WebServlet("/get-products")
 public final class GetItemTypesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson g = new Gson();
    String pageString = request.getParameter("page");
    int pageInt = Integer.valueOf(pageString);
    if (pageInt < 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    List<String> items;
    try {
      items = LibraryFunctions.getItemTypes(pageInt);
    } catch (SpannerException e) {
      throw new IOException("Failed to get Item Types");
    }
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(g.toJson(items));
  }

}
