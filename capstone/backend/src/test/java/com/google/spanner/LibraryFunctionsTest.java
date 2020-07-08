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

import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner.TransactionCallable;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;

import com.google.servlets.UserList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LibraryFunctionsTest {

  private static String DATABASE_INSTANCE = "capstone-instance";
  private static String DATABASE_NAME = "test-db";
  private static DatabaseClient databaseClient = null;

  @Before
  public void setUp() {
    LibraryFunctions.setDatabase(DATABASE_NAME);
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), DATABASE_INSTANCE, DATABASE_NAME);
    databaseClient = spanner.getDatabaseClient(db);
    insertData();
  }

  @After
  public void tearDown() {
    databaseClient.readWriteTransaction().run(
      new TransactionCallable<Void>() {
        @Override
        public Void run(TransactionContext transaction) throws Exception {
          String[] sql = {
            "DELETE FROM Inventory WHERE TRUE",
            "DELETE FROM Items WHERE TRUE",
            "DELETE FROM Stores WHERE TRUE",
            "DELETE FROM UserLists WHERE TRUE",
            "DELETE FROM Users WHERE TRUE"};
          for (String task : sql) {
            long rowCount = transaction.executeUpdate(Statement.of(task));
          }
          return null;
        }
      }
    );
  }

  public void insertData() {
    databaseClient.readWriteTransaction().run(
      new TransactionCallable<Void>() {
        @Override
        public Void run(TransactionContext transaction) throws Exception {
          String[] sql = {
            "INSERT INTO Items (ItemId, ItemNameAndBrand, ItemType) VALUES " +
              "(1, 'Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count', 'MILK'), " +
              "(2, 'Natrel Whole Milk, 32 fl oz', 'MILK'), " +
              "(3, 'FIJI Natural Artesian Water,16.9 Fl Oz, 24 Ct', 'WATER'), " +
              "(4, 'General Mills, Cheerios, Gluten Free, Breakfast Cereal, Family Size 18 oz Box', 'CEREAL'), " +
              "(5, 'OZARKA Brand 100% Natural Spring Water, 16.9-ounce plastic bottles', 'WATER') ", 
            "INSERT INTO Stores (StoreId, Address, StoreName) VALUES " +
              "(1, '3255 Mission College Blvd', 'Walmart'), " +
              "(2, '4080 Stevens Creek Blvd', 'Target'), " +
              "(3, '301 Ranch Dr', 'Whole Foods') ", 
            "INSERT INTO Inventory (StoreId, ItemId, ItemAvailability, Price) VALUES " +
              "(1, 1, 'AVAILABLE', 11.98), " +
              "(1, 2, 'AVAILABLE', 10.3), " +
              "(1, 3, 'AVAILABLE', 17.2), " +
              "(1, 5, 'AVAILABLE', 9.98), " +
              "(2, 1, 'AVAILABLE', 10.38), " +
              "(2, 4, 'AVAILABLE', 3.64), " +
              "(3, 2, 'AVAILABLE', 9.44), " +
              "(3, 4, 'AVAILABLE', 5.43) ", 
            "INSERT INTO Users (UserId, Email, Username) VALUES " +
              "(1, 'bzallen@google.com', 'brettallenyo'), " +
              "(2, 'pinkpanther@gmail.com', 'Pink Panther')", 
            "INSERT INTO UserLists (UserId, ListId, DisplayName, ItemTypes) VALUES " +
              "(1, 1, 'My List', ARRAY['Milk', 'Eggs', 'Bread']), " +
              "(2, 3, 'List Name', ARRAY['Butter', 'Juice', 'Peanuts']) " };
          for (String task : sql) {
            long rowCount = transaction.executeUpdate(Statement.of(task));
          }
          return null;
        }
      }
    );
  }
  
  @Test
  public void itemTypesCheck() {
    List<String> actual = LibraryFunctions.getItemTypes(0);
    List<String> expected = Arrays.asList("CEREAL", "MILK", "WATER");
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void userListsCheck() {
    List<UserList> actual = LibraryFunctions.getUserLists(1);
    UserList userList = new UserList(1, "My List", Arrays.asList("Milk", "Eggs", "Bread"));
    List<UserList> expected = Arrays.asList(userList);
    Assert.assertEquals(actual, expected);
  }
}