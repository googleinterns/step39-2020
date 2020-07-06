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

 @WebServlet("/api/v1/get-stores-with-items")
 public class GetStoresWithItemTypesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson g = new Gson();
    String itemTypesString = request.getParameter("item_types");
    List<String> itemTypes = Arrays.asList(itemTypesString.split("\\s*,\\s*"));
    List<Store> stores = new ArrayList<Store>();
    try {
      stores = getStores(itemTypes);
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().println(g.toJson(stores));
    } catch (SpannerException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }

  public List<Store> getStores(List<String> itemTypes) {
    return LibraryFunctions.getStoresWithItems(itemTypes);
  }

}
