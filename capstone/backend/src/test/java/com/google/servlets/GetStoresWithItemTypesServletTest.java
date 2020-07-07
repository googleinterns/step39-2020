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

import com.google.spanner.LibraryFunctions;
import java.io.IOException;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.Before;
import org.mockito.Mockito;

public class GetStoresWithItemTypesServletTest extends TestCase {
  private static final String ITEM_TYPES_KEY = "item_types";

  public void testGetStores() throws ServletException, IOException {
    String[] ITEM_TYPES = {"MILK"};
    HashMap<String, String[]> map = new HashMap<>();
    map.put(ITEM_TYPES_KEY, ITEM_TYPES);
    SetupObj setupObj = ServletTestUtil.setupMockDataGetList(map);

    GetStoresWithItemTypesServlet servlet = Mockito.spy(GetStoresWithItemTypesServlet.class);

    List<Store> fakeStores = new ArrayList<Store>();
    Store store1 = new Store(1, "Walmart", "3255 Mission College Blvd");
    Store store2 = new Store(2, "Target", "4080 Stevens Creek Blvd");
    Store store3 = new Store(3, "Whole Foods", "301 Ranch Dr");
    store1.addItem(1, 11.98, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store2.addItem(1, 10.38, "Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count");
    store3.addItem(2, 9.44, "Natrel Whole Milk, 32 fl oz");
    fakeStores.add(store1);
    fakeStores.add(store2);
    fakeStores.add(store3);

    Mockito.when(servlet.getStores(Arrays.asList("MILK"))).thenReturn(fakeStores);
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    String expected = "[{\"totalPrice\":11.98,\"storeId\":1,\"items\":[{\"itemId\":1,\"itemPrice\":11.98,\"itemName\":\"Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count\"}],\"storeAddress\":\"3255 Mission College Blvd\",\"storeName\":\"Walmart\"}," +
        "{\"totalPrice\":10.38,\"storeId\":2,\"items\":[{\"itemId\":1,\"itemPrice\":10.38,\"itemName\":\"Horizon Organic Whole Shelf-Stable Milk, 8 Oz., 12 Count\"}],\"storeAddress\":\"4080 Stevens Creek Blvd\",\"storeName\":\"Target\"}," +
        "{\"totalPrice\":9.44,\"storeId\":3,\"items\":[{\"itemId\":2,\"itemPrice\":9.44,\"itemName\":\"Natrel Whole Milk, 32 fl oz\"}],\"storeAddress\":\"301 Ranch Dr\",\"storeName\":\"Whole Foods\"}]";
    assertTrue("Wrong stores", result.contains(expected));
  }
}
