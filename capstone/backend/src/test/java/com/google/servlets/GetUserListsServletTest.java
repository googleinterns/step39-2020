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
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

public class GetUserListsServletTest extends TestCase {
  private static final String USER_ID_KEY = "userId";
  private static final String VALID_USER_ID = "2";
  private static final String EMPTY_USER_ID = "";

  public void testDoPostSucceed() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    map.put(USER_ID_KEY, VALID_USER_ID);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    GetUserListsServlet servlet = Mockito.spy(GetUserListsServlet.class);
    Mockito.doReturn(Arrays.asList(new UserList(2, "List Name", Arrays.asList("Milk", "Bread"))))
        .when(servlet)
        .getUserLists(2);
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
    assertTrue("Should contain userLists object", result.contains("\"userLists\":"));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    map.put(USER_ID_KEY, EMPTY_USER_ID);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    GetUserListsServlet servlet = new GetUserListsServlet();
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}
