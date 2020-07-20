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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;

@WebServlet("/api/v1/get-store-rankings")
public class GetStoreRankingsServlet extends HttpServlet {
  private static final String API_KEY = "INSERT_GOOGLE_API_KEY_HERE";
  private static final double AVERAGE_GAS_PRICE = 3.127;
  private static final double AVERAGE_MILES_PER_GALLON = 25.2;
  private static final double UNAVALIABLE_ITEMS_WEIGHT = 0.5;
  private static final String DESTINATIONS_PARAM = "destinations";
  private static final String API_KEY_PARAM = "key";
  private static final double MILES_METERS_CONVERSION = 1609.34;
  private static final String ORIGINS_PARAM = "origins";
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
    if (!distances.isEmpty()) {
      stores =
          stores.stream()
              .filter(
                  store ->
                      (distances.get(store.getStoreAddress())
                          < (userPreferences.getDistancePreference() * MILES_METERS_CONVERSION)))
              .collect(Collectors.toList());
    }
    rankStores(stores, distances);
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(g.toJson(stores));
  }

  /*
   * Link to the ranking algorithm: https://drive.google.com/file/d/1sM57YGHXXgQkO9VPvhHz2GzbnYoznHxE/view?usp=sharing
   */
  private void rankStores(List<Store> stores, Map<String, Integer> distances) {
    Collections.sort(
        stores,
        new Comparator<Store>() {
          @Override
          public int compare(Store s1, Store s2) {
            if (s1.getNumberOfItemsFound() > s2.getNumberOfItemsFound()) {
              return 1;
            } else if (s1.getNumberOfItemsFound() < s2.getNumberOfItemsFound()) {
              return -1;
            }
            double s1StoreScore =
                s1.getTotalUnavaliableItemsFound() * UNAVALIABLE_ITEMS_WEIGHT
                    + s1.getLowestPotentialPrice() * PRICE_WEIGHT;
            double s2StoreScore =
                s2.getTotalUnavaliableItemsFound() * UNAVALIABLE_ITEMS_WEIGHT
                    + s2.getLowestPotentialPrice() * PRICE_WEIGHT;
            if (!distances.isEmpty()) {
              s1StoreScore +=
                  distances.get(s1.getStoreAddress())
                      / MILES_METERS_CONVERSION
                      / AVERAGE_MILES_PER_GALLON
                      * AVERAGE_GAS_PRICE;
              s2StoreScore +=
                  distances.get(s2.getStoreAddress())
                      / MILES_METERS_CONVERSION
                      / AVERAGE_MILES_PER_GALLON
                      * AVERAGE_GAS_PRICE;
            }
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

  public Map<String, Integer> getDistances(
      List<String> addresses, Pair<Double, Double> userLocation) throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < addresses.size(); i++) {
      sb.append(addresses.get(i));
      if (i != addresses.size() - 1) {
        sb.append("|");
      }
    }
    try {
      URIBuilder ub = new URIBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
      ub.addParameter(ORIGINS_PARAM, userLocation.getLeft() + "," + userLocation.getRight());
      ub.addParameter(DESTINATIONS_PARAM, sb.toString());
      ub.addParameter(API_KEY_PARAM, API_KEY);
      System.out.println(ub.toString());
    } catch (URISyntaxException e) {
      return new HashMap<>();
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
      return new HashMap<>();
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
