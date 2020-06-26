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

package com.java.spanner;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.ServiceOptions;
import java.lang.Exception;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class LibraryFunctions {
  private static String DATABASE_INSTANCE = "capstone-instance";
  private static String DATABASE_NAME = "step39-db";

  private static DatabaseClient databaseClient = null;

  private static DatabaseClient initClient() {
    if(databaseClient == null) {
      SpannerOptions options = SpannerOptions.newBuilder().build();
      Spanner spanner = options.getService();

      DatabaseId db = DatabaseId.of(options.getProjectId(), DATABASE_INSTANCE, DATABASE_NAME);
      databaseClient = spanner.getDatabaseClient(db);
    }
    return databaseClient;
  }

  public static boolean writeUserLists(int userId, int listId, List<String> itemTypes) {
    try {
      DatabaseClient dbClient = initClient();
      List<Mutation> mutations = Arrays.asList(
        Mutation.newInsertOrUpdateBuilder("UserLists")
          .set("UserId")
          .to(userId)
          .set("ListId")
          .to(listId)
          .set("ItemTypes")
          .toStringArray(itemTypes)
          .build());
      dbClient.write(mutations);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public static List<List<String>> getUserLists(int userId) {
    DatabaseClient dbClient = initClient();
    Statement s = 
        Statement.newBuilder(
                "SELECT ItemTypes "
                    + "FROM UserLists "
                    + "WHERE UserId = @userId")
            .bind("userId")
            .to(userId)
            .build();
    List<List<String>> userLists = new ArrayList<>();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(s)) {
        while (resultSet.next()) {
            userLists.add(resultSet.getStringList("ItemTypes"));
        }
    }
    return userLists;
  }

  public static boolean createUser(int userId, String userName, String email) {
    try {
      DatabaseClient dbClient = initClient();
      Mutation mutation = Mutation.newInsertBuilder("Users")
                            .set("UserId")
                            .to(userId)
                            .set("UserName")
                            .to(userName)
                            .set("Email")
                            .to(email)
                            .build();
      dbClient.write(Arrays.asList(mutation));
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public static List<String> getItemTypes(int page) {
    DatabaseClient dbClient = initClient();
    List<String> itemTypes = new ArrayList<String>();
    try (ResultSet resultSet =
      dbClient
          .singleUse() // Execute a single read or query against Cloud Spanner.
          .executeQuery(Statement.of("SELECT DISTINCT ItemType FROM Items ORDER BY ItemType"))) {
      for (int i = 0; resultSet.next() && i < (page+1)*10; i++) {
        if (i >= page*10) {
            itemTypes.add(resultSet.getString(0));
        }
      }
    }
    return itemTypes;
  }
}