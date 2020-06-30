package com.google.servlets;

import org.mockito.Mockito;
import java.io.IOException;
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

    GetUserListsServlet servlet = new GetUserListsServlet();
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

    Mockito.verify(setupObj.response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}