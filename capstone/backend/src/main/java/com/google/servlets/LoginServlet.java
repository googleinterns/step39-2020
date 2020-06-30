package com.google.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet that returns an object containing login informaton about the user.
 */
@WebServlet("/api/v1/login")
public class LoginServlet extends HttpServlet {
  private static final String LOGIN_REDIRECT = "/";
  private static final String LOGOUT_REDIRECT = "/";

  private class UserLogin {
    private boolean isLoggedIn;
    private String loginUrl;
    private String logoutUrl;
    private String email;

    public UserLogin(boolean isLoggedIn, String loginUrl) {
      this.isLoggedIn = isLoggedIn;
      this.loginUrl = loginUrl;
    }

    public UserLogin(boolean isLoggedIn, String logoutUrl, String email) {
        this.isLoggedIn = isLoggedIn;
        this.logoutUrl = logoutUrl;
        this.email = email;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    Gson gson = new Gson();
    UserLogin userLogin;

    if (userService.isUserLoggedIn()) {
      userLogin = new UserLogin(/* User logged in=*/true, 
        userService.createLogoutURL(LOGOUT_REDIRECT), userService.getCurrentUser().getEmail());
    } else {
      userLogin = new UserLogin(/* User logged in=*/false, 
        userService.createLoginURL(LOGIN_REDIRECT));
    }

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(userLogin));
  }
}