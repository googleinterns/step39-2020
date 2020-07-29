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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class ShareViaEmailServletTest extends TestCase {

  private static final String CORRECT_REQUEST_STRING =
      "{\"email\":\"bzallen@google.com\", \"latitude\":37.2695392, \"longitude\": -121.77117759999999, "
          + " \"itemTypes\" : [\"cereal\", \"chips\", \"cookies\", \"flour\", \"ketchup\", \"milk\", \"napkin\"], "
          + "\"stores\" : [{\"distanceFromUser\" : 10, \"items\" : {\"chips\": []}, \"lowestPotentialPrice\" : 15, \"storeAddress\" : \"5100 Broadway\", \"storeId\" : 10, \"storeName\" : \"Safeway-94611\", \"totalItemsFound\" : 6, \"totalUnavailableItemsFound\" : 0, \"typeToPrice\" : {\"chips\" : 3}}]}";
  private static final String INCORRECT_REQUEST_STRING =
      "{\"email\":\"bademail\", \"html\":\"<p>HTML is fine</p>\"}";

  public void testDoPostSucceed() throws ServletException, IOException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(CORRECT_REQUEST_STRING);

    ShareViaEmailServlet servlet = new ShareViaEmailServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(INCORRECT_REQUEST_STRING);

    ShareViaEmailServlet servlet = new ShareViaEmailServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}
