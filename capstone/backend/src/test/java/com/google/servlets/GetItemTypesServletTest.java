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
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.Before;
import org.mockito.Mockito;

public class GetItemTypesServletTest extends TestCase {
  private static final String PAGE_KEY = "page";

  @Before
  public void setUp() {
    LibraryFunctions.setDatabase("test-db");
  }

  public void testGetItems() throws ServletException, IOException {
    String PAGE = "0";
    HashMap<String, String> map = new HashMap<>();
    map.put(PAGE_KEY, PAGE);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    GetItemTypesServlet servlet = Mockito.spy(GetItemTypesServlet.class);
    Mockito.doReturn(Arrays.asList("CEREAL", "CHEESE", "MILK", "WATER"))
        .when(servlet)
        .getItemTypes();
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Wrong item types", result.contains("[\"CEREAL\",\"CHEESE\",\"MILK\",\"WATER\"]"));
  }
}
