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

import com.google.servlets.UserList;
import com.google.servlets.Store;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LibraryFunctionsTest {
  
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
    Store store4 = new Store(1, "Walmart", "3255 Mission College Blvd");
    store1.addItem(1, 11.98, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store2.addItem(1, 10.38, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store3.addItem(2, 9.44, "Natrel Whole Milk, 32 fl oz");
    store4.addItem(2, 10.3, "Natrel Whole Milk, 32 fl oz");
    expected.add(store1);
    expected.add(store2);
    expected.add(store3);
    Assert.assertEquals(4, actual.size());
    Collections.sort(actual);
    Collections.sort(expected);
    for(int i = 0; i < expected.size(); i++){
      Assert.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }
  
  @Test
  public void getStoresForTwoItemTypes() {
    List<String> itemTypes = Arrays.asList("MILK", "WATER");
    List<Store> actual = LibraryFunctions.getStoresWithItems(itemTypes);
    List<Store> expected = new ArrayList<Store>();
    Store store1 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store2 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store3 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store4 = new Store(1, "Walmart", "3255 Mission College Blvd");
    store1.addItem(1, 11.98, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store1.addItem(3, 17.2, "FIJI Natural Artesian Water,16.9 Fl Oz, 24 Ct");
    store2.addItem(1, 11.98, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store2.addItem(5, 9.98, "OZARKA Brand 100% Natural Spring Water, 16.9-ounce plastic bottles");
    store3.addItem(2, 10.3, "Natrel Whole Milk, 32 fl oz");
    store3.addItem(3, 17.2, "FIJI Natural Artesian Water,16.9 Fl Oz, 24 Ct");
    store4.addItem(2, 10.3, "Natrel Whole Milk, 32 fl oz");
    store4.addItem(5, 9.98, "OZARKA Brand 100% Natural Spring Water, 16.9-ounce plastic bottles");
    expected.add(store1);
    Assert.assertEquals(4, actual.size());
    Collections.sort(actual);
    Collections.sort(expected);
    for(int i = 0; i < expected.size(); i++){
      Assert.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }
}