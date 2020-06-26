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

import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.DatabaseClient;
import java.util.Arrays;

public class SpannerUtilFunctions {
  private String DATABASE_INSTANCE = "Capstone Instance";
  private String DATABASE_NAME = "step39-db";

  private DatabaseClient initClient() {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();

    DatabaseId db = DatabaseId.of(options.getProjectId(), DATABASE_INSTANCE, DATABASE_NAME);
    return spanner.getDatabaseClient(db);
  }

  public void writeUserLists(int userId, int listId, List<String> itemTypes) {
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
  }

   private List<List<String>> getUserLists(int userId) {
    DatabaseClient dbClient = initClient();
    Statment statment = 
        Statment.newBuilder(
                "SELECT ItemTypes "
                    + "FROM UserLists"
                    + "WHERE UserId = @userId")
            .bind("userId")
            .to(userId)
            .build();
    List<List<String>> userLists = new ArrayList<>();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statment)) {
        while (resultSet.next()) {
            userLists.add(resultSet.getStringList("ItemTypes"));
        }
    }
    return userLists;
  }

  private void createUser(int userId, String userName, String email) {
    DatabaseClient = initClient();
    Mutation mutation = Mutation.newInsertBuilder("Users")
                            .set("UserId")
                            .to(userId)
                            .set("UserName")
                            .to(userName)
                            .set("Email")
                            .to(email)
                            .build();
    dbClient.write(Arrays.asList(mutation));
  }

}