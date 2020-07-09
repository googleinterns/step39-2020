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

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockito.Mockito;

class ServletTestUtil {
  private ServletTestUtil() {}

  public static boolean isValidJson(String str) {
    Gson g = new Gson();
    try {
      g.fromJson(str, Object.class);
      return true;
    } catch (com.google.gson.JsonSyntaxException e) {
      return false;
    }
  }

  public static SetupObj setupMockDataPost(String reqString) throws IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    StringReader reader = new StringReader(reqString);
    Mockito.when(req.getMethod()).thenReturn("POST");
    Mockito.when(req.getReader()).thenReturn(new BufferedReader(reader));
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));
    return new SetupObj(req, res, writer);
  }

  public static SetupObj setupMockDataGet(Map<String, String> params) throws IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    Mockito.when(req.getMethod()).thenReturn("GET");
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));
    for (String key : params.keySet()) {
      Mockito.when(req.getParameter(key)).thenReturn(params.get(key));
    }
    return new SetupObj(req, res, writer);
  }

  public static SetupObj setupMockDataGetList(Map<String, String[]> params) throws IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    Mockito.when(req.getMethod()).thenReturn("GET");
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));   
    for (String key : params.keySet()) {
      Mockito.when(req.getParameterValues(key)).thenReturn(params.get(key));
    }
    return new SetupObj(req, res, writer);
  }
}
