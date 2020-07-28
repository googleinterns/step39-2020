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
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class RemoveUserlistServletTest extends TestCase {
  private static final String EMPTY_VALUE = "";
  private static final String LIST_ID_KEY = "listId";
  private static final String USER_ID_KEY = "userId";
  private static final String VALID_USER_ID = "2";
  private static final String VALID_LIST_ID = "3";

  public void testDoGetSuceed() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    map.put(LIST_ID_KEY, VALID_LIST_ID);
    map.put(USER_ID_KEY, VALID_USER_ID);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    RemoveUserlistServlet servlet = Mockito.spy(RemoveUserlistServlet.class);
    Mockito.doNothing()
        .when(servlet)
        .deleteUserlist(Long.parseLong(VALID_LIST_ID), Long.parseLong(VALID_USER_ID));
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
  }

  public void testDoGetBadRequest() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    map.put(LIST_ID_KEY, EMPTY_VALUE);
    map.put(USER_ID_KEY, EMPTY_VALUE);
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    RemoveUserlistServlet servlet = new RemoveUserlistServlet();
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}
