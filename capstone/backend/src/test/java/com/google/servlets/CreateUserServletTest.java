package com.google.servlets;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class CreateUserServletTest extends TestCase {
  private static final String ID_TOKEN_STRING = "testIdTokenString";
  private static final String CORRECT_REQUEST_STRING = "{\"idTokenString\": \"" + ID_TOKEN_STRING + "\"}";
  private static final String INCORRECT_REQUEST_STRING = "{list:\"bread\"}";

  public void testDoPostSucceed() throws ServletException, IOException, GeneralSecurityException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(CORRECT_REQUEST_STRING);
    Payload payload = new Payload();
    payload.setSubject("abcd1234");
    payload.set("email", "test@gmail.com");
    payload.set("name", "Test User");
    GoogleIdToken idToken = Mockito.mock(GoogleIdToken.class);
    GoogleIdTokenVerifier verifier = Mockito.mock(GoogleIdTokenVerifier.class);
    Mockito.when(idToken.getPayload()).thenReturn(payload);
    Mockito.when(verifier.verify(ID_TOKEN_STRING)).thenReturn(idToken);

    CreateUserServlet servlet = new CreateUserServlet();
    servlet.setVerifier(verifier);
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid josn format", ServletTestUtil.isValidJson(result));
    assertTrue("Should contain userid in object", result.contains("\"userId\":"));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(INCORRECT_REQUEST_STRING);

    CreateUserServlet servlet = new CreateUserServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
} 