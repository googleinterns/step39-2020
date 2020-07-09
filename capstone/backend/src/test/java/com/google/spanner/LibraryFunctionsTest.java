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
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner.TransactionCallable;
import com.google.servlets.Store;
import com.google.servlets.UserList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            "DELETE FROM Inventories WHERE TRUE",
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
            "INSERT INTO Items (ItemId, ItemName, ItemBrand, ItemType) VALUES " +
              "('1', 'Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count', 'Horizon', 'MILK'), " +
              "('2', 'Whole Milk, 32 fl oz', 'Natrel', 'MILK'), " +
              "('3', 'Natural Artesian Water,16.9 Fl Oz, 24 Ct', 'FIJI', 'WATER'), " +
              "('4', 'Cheerios, Gluten Free, Breakfast Cereal, Family Size 18 oz Box', 'General Mills', 'CEREAL'), " +
              "('5', '100% Natural Spring Water, 16.9-ounce plastic bottles', 'OZARKA', 'WATER') ", 
            "INSERT INTO Stores (StoreId, Address, StoreName) VALUES " +
              "(1, '3255 Mission College Blvd', 'Walmart'), " +
              "(2, '4080 Stevens Creek Blvd', 'Target'), " +
              "(3, '301 Ranch Dr', 'Whole Foods') ", 
            "INSERT INTO Inventories (StoreId, ItemId, ItemAvailability, Price) VALUES " +
              "(1, '1', 'AVAILABLE', 11.98), " +
              "(1, '2', 'AVAILABLE', 10.3), " +
              "(1, '3', 'AVAILABLE', 17.2), " +
              "(1, '5', 'AVAILABLE', 9.98), " +
              "(2, '1', 'AVAILABLE', 10.38), " +
              "(2, '4', 'AVAILABLE', 3.64), " +
              "(3, '2', 'AVAILABLE', 9.44), " +
              "(3, '4', 'AVAILABLE', 5.43) ", 
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

  @Test
  public void getStoresForOneItemType() {
    List<String> itemTypes = Arrays.asList("MILK");
    List<Store> actual = LibraryFunctions.getStoresWithItems(itemTypes);
    List<Store> expected = new ArrayList<Store>();
    Store store1 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store2 = new Store(2, "Target", "4080 Stevens Creek Blvd");
    Store store3 = new Store(3, "Whole Foods", "301 Ranch Dr");
    store1.addItem("1", 11.98, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store1.addItem("2", 10.3, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    store2.addItem("1", 10.38, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store3.addItem("2", 9.44, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    expected.add(store1);
    expected.add(store2);
    expected.add(store3);
    Assert.assertEquals(3, actual.size());
    Collections.sort(actual);
    Collections.sort(expected);
    for (int i = 0; i < expected.size(); i++) {
      Assert.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }

  @Test
  public void getStoresForTwoItemTypes() {
    List<String> itemTypes = Arrays.asList("MILK", "WATER");
    List<Store> actual = LibraryFunctions.getStoresWithItems(itemTypes);
    List<Store> expected = new ArrayList<Store>();
    Store store1 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store2 = new Store(2, "Target", "4080 Stevens Creek Blvd");
    Store store3 = new Store(3, "Whole Foods", "301 Ranch Dr");
    store1.addItem("1", 11.98, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store1.addItem("2", 10.3, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    store1.addItem("3", 17.2, "Natural Artesian Water,16.9 Fl Oz, 24 Ct", "FIJI", "WATER");
    store2.addItem("5", 9.98, "100% Natural Spring Water, 16.9-ounce plastic bottles", "OZARKA", "WATER");
    store2.addItem("1", 10.38, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store3.addItem("2", 9.44, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    expected.add(store1);
    expected.add(store2);
    expected.add(store3);
    Assert.assertEquals(3, actual.size());
    Collections.sort(actual);
    Collections.sort(expected);
    for (int i = 0; i < expected.size(); i++) {
      Assert.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }
}
