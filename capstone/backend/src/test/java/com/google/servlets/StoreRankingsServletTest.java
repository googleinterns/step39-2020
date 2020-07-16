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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class StoreRankingsServletTest extends TestCase {
  private static final String USER_PREFERENCES_KEY = "user-preferences";
  private static final String USER_PREFERENCES_STRING =
      "{\"latitude\":1.234,\"longitude\":1.234,\"distancePreference\":4,\"selectedItemTypes\":[\"MILK\"]}";

  public void testRankStores() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    map.put(USER_PREFERENCES_KEY, USER_PREFERENCES_STRING);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    StoreRankingsServlet servlet = Mockito.spy(StoreRankingsServlet.class);

    List<Store> fakeStores = new ArrayList<Store>();
    Store store1 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store2 = new Store(2, "Target", "4080 Stevens Creek Blvd");
    Store store3 = new Store(3, "Whole Foods", "301 Ranch Dr");
    store1.addItem(
        "1", 11.98, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store1.addItem("2", 10.3, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    store2.addItem(
        "1", 10.38, "Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count", "Horizon", "MILK");
    store3.addItem("2", 9.44, "Whole Milk, 32 fl oz", "Natrel", "MILK");
    fakeStores.add(store1);
    fakeStores.add(store2);
    fakeStores.add(store3);
    List<String> fakeStoreAddresses =
        fakeStores.stream().map(store -> store.getStoreAddress()).collect(Collectors.toList());
    HashMap<String, Integer> fakeDistances = new HashMap<>();
    fakeDistances.put(fakeStoreAddresses.get(0), 30);
    fakeDistances.put(fakeStoreAddresses.get(1), 45);
    fakeDistances.put(fakeStoreAddresses.get(2), 60);

    Mockito.when(servlet.getStores(Arrays.asList("MILK"))).thenReturn(fakeStores);
    System.out.println("Here: " + fakeStoreAddresses.toString());
    Mockito.when(servlet.getDistances(fakeStoreAddresses, Arrays.asList(1.234, 1.234)))
        .thenReturn(fakeDistances);
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    String expected =
        "[{\"lowestPotentialPrice\":22.28,\"totalItemsFound\":1,\"storeId\":1,\"items\":{\"MILK\":[{\"itemId\":\"1\",\"storeId\":1,\"itemPrice\":11.98,\"itemBrand\":\"Horizon\",\"itemName\":\"Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count\"},{\"itemId\":\"2\",\"storeId\":1,\"itemPrice\":10.3,\"itemBrand\":\"Natrel\",\"itemName\":\"Whole Milk, 32 fl oz\"}]},\"typeToPrice\":{\"MILK\":10.3},\"storeAddress\":\"3255 Mission College Blvd\",\"storeName\":\"Walmart\"},"
            + "{\"lowestPotentialPrice\":10.38,\"totalItemsFound\":1,\"storeId\":2,\"items\":{\"MILK\":[{\"itemId\":\"1\",\"storeId\":2,\"itemPrice\":10.38,\"itemBrand\":\"Horizon\",\"itemName\":\"Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count\"}]},\"typeToPrice\":{\"MILK\":10.38},\"storeAddress\":\"4080 Stevens Creek Blvd\",\"storeName\":\"Target\"},"
            + "{\"lowestPotentialPrice\":9.44,\"totalItemsFound\":1,\"storeId\":3,\"items\":{\"MILK\":[{\"itemId\":\"2\",\"storeId\":3,\"itemPrice\":9.44,\"itemBrand\":\"Natrel\",\"itemName\":\"Whole Milk, 32 fl oz\"}]},\"typeToPrice\":{\"MILK\":9.44},\"storeAddress\":\"301 Ranch Dr\",\"storeName\":\"Whole Foods\"}]";
    assertTrue("Wrong stores", result.contains(expected));
  }
}
