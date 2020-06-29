package com.google.servlets;

import com.google.gson.Gson;
import org.mockito.Mockito;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ServletTestUtil {
  private ServletTestUtil() {}

  public static boolean isValidJson(String str) {
    Gson g = new Gson();
    try {
      g.fromJson(str, Object.class);
      return true;
    } catch(com.google.gson.JsonSyntaxException e) { 
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
}