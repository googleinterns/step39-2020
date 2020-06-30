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

import org.mockito.Mockito;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

public class ProductDataServletTest extends TestCase {
  private static final String PAGE_KEY = "page";
  private static final int PAGE = 0;

  public void testDoPostSucceed() throws ServletException, IOException {
    HashMap<String, Integer> map = new HashMap<>();
    map.put(PAGE_KEY, PAGE);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    ProductDataServlet servlet = new ProductDataServlet();
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Should contain userLists object", result.contains("UserLists:"));
  }
}
