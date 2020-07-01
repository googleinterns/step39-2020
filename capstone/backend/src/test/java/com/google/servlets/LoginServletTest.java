package com.google.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

public class LoginServletTest extends TestCase {
  private static final String DOMAIN_NAME = "gmail.com";
  private static final String LOGIN_URL = "/_ah/login?continue";
  private static final String LOGOUT_URL = "/_ah/logout?continue";
  private static final String REDIRECT_URL = "/";
  private static final String TEST_EMAIL = "test-user@gmail.com";
    
  public void testDoGetLoggedIn() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);
    User user = new User(TEST_EMAIL, DOMAIN_NAME);
    UserService userService = Mockito.mock(UserService.class);
    Mockito.when(userService.isUserLoggedIn()).thenReturn(true);
    Mockito.when(userService.createLogoutURL(REDIRECT_URL)).thenReturn(LOGOUT_URL);
    Mockito.when(userService.getCurrentUser()).thenReturn(user);

    LoginServlet servlet = new LoginServlet();
    servlet.setUserService(userService);
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
    assertTrue("Should contain email", result.contains(TEST_EMAIL));
    assertTrue("Login status should be true", result.contains("\"isLoggedIn\":true"));
    assertTrue("Should contain logoutUrl", result.contains(LOGOUT_URL));
  }

  public void testDoGetNotLoggedIn() throws ServletException, IOException {
    HashMap<String, String> map = new HashMap<>();
    SetupObj setupObj = ServletTestUtil.setupMockDataGet(map);
    UserService userService = Mockito.mock(UserService.class);
    Mockito.when(userService.isUserLoggedIn()).thenReturn(false);
    Mockito.when(userService.createLoginURL(REDIRECT_URL)).thenReturn(LOGIN_URL);

    LoginServlet servlet = new LoginServlet();
    servlet.setUserService(userService);
    servlet.doGet(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
    assertTrue("Login status should be false", result.contains("\"isLoggedIn\":false"));
    assertTrue("Should contain loginUrl", result.contains(LOGIN_URL));
  }
}