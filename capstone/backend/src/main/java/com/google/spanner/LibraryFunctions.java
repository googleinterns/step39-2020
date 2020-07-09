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

package com.google.spanner;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Value;
import com.google.servlets.Store;
import com.google.servlets.UserList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LibraryFunctions {
  private static String DATABASE_INSTANCE = "capstone-instance";
  private static String DATABASE_NAME = "step39-db";
  
  private static final String ADDRESS =             "Address";
  private static final String EMAIL =               "Email";
  private static final String ITEM_ID =             "ItemId";
  private static final String ITEM_NAME_AND_BRAND = "ItemNameAndBrand";
  private static final String ITEM_TYPE =           "ItemType";
  private static final String ITEM_TYPES =          "ItemTypes";
  private static final String LIST_ID =             "ListId";
  private static final String DISPLAY_NAME =        "DisplayName";
  private static final String PRICE =               "Price";
  private static final String STORE_ID =            "StoreId";
  private static final String STORE_NAME =          "StoreName";
  private static final String USER_ID =             "UserId";
  private static final String USER_LISTS =          "UserLists";
  private static final String USERNAME =            "Username";
  private static final String USERS =               "Users";


  private static DatabaseClient databaseClient = null;

  private LibraryFunctions() {}

  private static DatabaseClient initClient() {
    if (databaseClient == null) {
      SpannerOptions options = SpannerOptions.newBuilder().build();
      Spanner spanner = options.getService();

      DatabaseId db = DatabaseId.of(options.getProjectId(), DATABASE_INSTANCE, DATABASE_NAME);
      databaseClient = spanner.getDatabaseClient(db);
    }
    return databaseClient;
  }

  public static void setDatabase(String dbName) {
    DATABASE_NAME = dbName;
  }

  public static void writeUserLists(
      long userId, long listId, List<String> itemTypes, String displayName)
      throws SpannerException {
    DatabaseClient dbClient = initClient();
    List<Mutation> mutations = Arrays.asList(
      Mutation.newInsertOrUpdateBuilder(USER_LISTS)
          .set(USER_ID)
          .to(userId)
          .set(LIST_ID)
          .to(listId)
          .set(ITEM_TYPES)
          .toStringArray(itemTypes)
          .set(DISPLAY_NAME)
          .to(displayName)
          .build());
    dbClient.write(mutations);
  }

  public static List<UserList> getUserLists(long userId) throws SpannerException {
    DatabaseClient dbClient = initClient();
    String query = "SELECT ItemTypes, DisplayName, ListId FROM UserLists WHERE UserId = @userId";
    Statement s = Statement.newBuilder(query).bind("userId").to(userId).build();
    List<UserList> userLists = new ArrayList<>();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(s)) {
        while (resultSet.next()) {
            userLists.add(new UserList(resultSet.getLong(LIST_ID), 
            resultSet.getString(DISPLAY_NAME), resultSet.getStringList(ITEM_TYPES)));
        }
    }
    return userLists;
  }

  public static void createUser(long userId, String userName, String email)
      throws SpannerException {
    DatabaseClient dbClient = initClient();
    Mutation mutation = Mutation.newInsertBuilder(USERS)
                            .set(USER_ID)
                            .to(userId)
                            .set(USERNAME)
                            .to(userName)
                            .set(EMAIL)
                            .to(email)
                            .build();
    dbClient.write(Arrays.asList(mutation));
  }

  public static List<String> getItemTypes(int page) throws SpannerException {
    DatabaseClient dbClient = initClient();
    List<String> itemTypes = new ArrayList<String>();
    String query = "SELECT DISTINCT ItemType FROM Items ORDER BY ItemType";
    try (ResultSet resultSet =
        dbClient
            .singleUse() // Execute a single read or query against Cloud Spanner.
            .executeQuery(Statement.of(query))) {
      for (int i = 0; resultSet.next() && i < (page + 1) * 10; i++) {
        if (i >= page * 10) {
          itemTypes.add(resultSet.getString(0));
        }
      }
    }
    return itemTypes;
  }

  /*
   * Provided a list of ItemTypes, this method will return a list of Stores
   * that contain one or more of the Item Types provided.
   *
   * @param itemTypes list of Strings that describes the desired Item Types
   * @return a list of all stores that have one or more Item Type
   *
   */
  public static List<Store> getStoresWithItems(List<String> itemTypes) {
    Map<Long, Store> stores = new HashMap<Long, Store>();
    DatabaseClient dbClient = initClient();
    Value itemListArray = Value.stringArray(itemTypes);
    String query = "SELECT a.ItemId, a.ItemNameAndBrand, a.ItemType, b.StoreId, b.Price, c.Address, c.StoreName " +
        "FROM Items a JOIN Inventory b ON a.ItemId = b.ItemId JOIN Stores c ON b.StoreId = c.StoreId " +
        "WHERE b.ItemAvailability = 'AVAILABLE' AND a.ItemType IN UNNEST(@itemTypes)";
    Statement statement = Statement.newBuilder(query).bind("itemTypes").to(itemListArray).build();
    try(ResultSet allInfo = dbClient.singleUse().executeQuery(statement)) {
      while(allInfo.next()) {
        long itemId = allInfo.getLong(ITEM_ID);
        String itemName = allInfo.getString(ITEM_NAME_AND_BRAND);
        String itemType = allInfo.getString(ITEM_TYPE);
        long storeId = allInfo.getLong(STORE_ID);
        double itemPrice = allInfo.getDouble(PRICE);
        String storeAddress = allInfo.getString(ADDRESS);
        String storeName = allInfo.getString(STORE_NAME);
        if(stores.containsKey(storeId)) {
          stores.get(storeId).addItem(itemId, itemPrice, itemName, itemType);
        } else {
          Store newStore = new Store(storeId, storeName, storeAddress);
          stores.put(storeId, newStore);
        }
      }
    }
    return new ArrayList<Store>(stores.values());
  }

}
