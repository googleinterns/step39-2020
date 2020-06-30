package com.google.servlets;

import org.mockito.Mockito;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

public class LoginServletTest extends TestCase {
  public void testDoPostSucceed() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);

    LoginServlet servlet = new LoginServlet();
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
    assertTrue("Should contain loginUrl", result.contains("\"loginUrl\":"));
  }
}