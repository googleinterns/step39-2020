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

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.servlets.UserList;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryFunctions {
  private static String DATABASE_INSTANCE = "capstone-instance";
  private static String DATABASE_NAME = "step39-db";

  private static final String EMAIL = "Email";
  private static final String ITEMTYPES = "ItemTypes";
  private static final String LISTID = "ListId";
  private static final String LISTNAME = "DisplayName";
  private static final String USERID = "UserId";
  private static final String USERLISTS = "UserLists";
  private static final String USERNAME = "Username";
  private static final String USERS = "Users";

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

  public static void writeUserLists(long userId, long listId, List<String> itemTypes,
      String displayName) throws SpannerException {
    DatabaseClient dbClient = initClient();
    List<Mutation> mutations = Arrays.asList(Mutation.newInsertOrUpdateBuilder(USERLISTS)
                                                 .set(USERID)
                                                 .to(userId)
                                                 .set(LISTID)
                                                 .to(listId)
                                                 .set(ITEMTYPES)
                                                 .toStringArray(itemTypes)
                                                 .set(LISTNAME)
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
        userLists.add(new UserList(resultSet.getLong(LISTID), resultSet.getString(LISTNAME),
            resultSet.getStringList(ITEMTYPES)));
      }
    }
    return userLists;
  }

  public static void createUser(long userId, String userName, String email)
      throws SpannerException {
    DatabaseClient dbClient = initClient();
    Mutation mutation = Mutation.newInsertBuilder(USERS)
                            .set(USERID)
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
}
