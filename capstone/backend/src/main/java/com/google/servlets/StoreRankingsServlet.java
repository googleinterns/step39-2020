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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.cloud.spanner.SpannerException;
import com.google.gson.Gson;
import com.google.spanner.LibraryFunctions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/get-store-rankings")
public class StoreRankingsServlet extends HttpServlet {
  private static final String API_KEY = "AIzaSyBCbZO-bCdkNzuCehg4mEQsSnHS1k7Unco";
  private static final double AVALIABLE_ITEMS_WEIGHT = 3;
  private static final double DISTANCE_WEIGHT = -0.0005;
  private static final double PRICE_WEIGHT = -1;
  private Gson g = new Gson();

  private class DistanceResponse {
    private class Row {
      private List<Element> elements;

      private class Element {
        private Distance distance;
        private Duration duration;
        private String status;

        private class Distance {
          String text;
          String value;
        }

        private class Duration {
          String text;
          String value;
        }
      }
    }

    private String status;
    private List<String> origin_addresses;
    private List<String> destination_addresses;
    private List<Row> rows;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserPreferences userPreferences = getUserPreferencesAndValidate(request);
    if (userPreferences == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    List<Store> stores = new ArrayList<Store>();
    try {
      stores = getStores(userPreferences.getSelectedItemTypes());
    } catch (SpannerException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      return;
    }
    Map<String, Integer> distances =
        getDistances(
            stores.stream().map(store -> store.getStoreAddress()).collect(Collectors.toList()),
            userPreferences.getLocation());
    rankStores(stores, distances);
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(g.toJson(stores));
  }

  private void rankStores(List<Store> stores, Map<String, Integer> distances) {
    Collections.sort(
        stores,
        new Comparator<Store>() {
          @Override
          public int compare(Store s1, Store s2) {
            double s1StoreScore =
                s1.getNumberOfItemsFound() * AVALIABLE_ITEMS_WEIGHT
                    + s1.getLowestPotentialPrice() * PRICE_WEIGHT
                    + distances.get(s1.getStoreAddress()) * DISTANCE_WEIGHT;
            double s2StoreScore =
                s2.getNumberOfItemsFound() * AVALIABLE_ITEMS_WEIGHT
                    + s2.getLowestPotentialPrice() * PRICE_WEIGHT
                    + distances.get(s2.getStoreAddress()) * DISTANCE_WEIGHT;
            if (s1StoreScore < s2StoreScore) {
              return -1;
            } else if (s1StoreScore > s2StoreScore) {
              return 1;
            } else {
              return 0;
            }
          }
        });
  }

  public Map<String, Integer> getDistances(List<String> addresses, List<Double> userLocation)
      throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    StringBuilder sb =
        new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
    sb.append(userLocation.get(0) + "," + userLocation.get(1) + "&destinations=");
    for (int i = 0; i < addresses.size(); i++) {
      sb.append(addresses.get(i).replace(' ', '+'));
      if (i == addresses.size() - 1) {
        sb.append("&key=" + API_KEY);
      } else {
        sb.append("|");
      }
    }

    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(sb.toString()));
    String response = request.execute().parseAsString();
    DistanceResponse distanceResponse = g.fromJson(response, DistanceResponse.class);
    try {
      return IntStream.range(0, addresses.size())
          .boxed()
          .collect(
              Collectors.toMap(
                  i -> addresses.get(i),
                  i ->
                      Integer.parseInt(
                          distanceResponse.rows.get(0).elements.get(i).distance.value)));
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      return IntStream.range(0, addresses.size())
          .boxed()
          .collect(Collectors.toMap(i -> addresses.get(i), i -> 0));
    }
  }

  public UserPreferences getUserPreferencesAndValidate(HttpServletRequest request) {
    UserPreferences userPreferences =
        g.fromJson(request.getParameter("user-preferences"), UserPreferences.class);
    if (userPreferences == null
        || userPreferences.getSelectedItemTypes() == null
        || userPreferences.getDistancePreference() == 0) {
      return null;
    }
    return userPreferences;
  }

  public List<Store> getStores(List<String> itemTypes) {
    return LibraryFunctions.getStoresWithItems(itemTypes);
  }
}
