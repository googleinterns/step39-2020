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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class CreateUserServletTest extends TestCase {
  private static final String CORRECT_REQUEST_STRING = "{\"idTokenString\": \"testIdTokenString\"}";
  private static final String EMAIL = "test@gmail.com";
  private static final String EMAIL_KEY = "email";
  private static final String GOOGLE_USER_ID = "abcd1234";
  private static final String ID_TOKEN_STRING = "testIdTokenString";
  private static final String INCORRECT_REQUEST_STRING = "{list:\"bread\"}";
  private static final String NAME = "Test User";
  private static final String NAME_KEY = "name";

  public void testDoPostSucceed() throws ServletException, IOException, GeneralSecurityException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(CORRECT_REQUEST_STRING);
    Payload payload = new Payload();
    payload.setSubject(GOOGLE_USER_ID);
    payload.set(EMAIL_KEY, EMAIL);
    payload.set(NAME_KEY, NAME);
    GoogleIdToken idToken = Mockito.mock(GoogleIdToken.class);
    GoogleIdTokenVerifier verifier = Mockito.mock(GoogleIdTokenVerifier.class);
    Mockito.when(idToken.getPayload()).thenReturn(payload);
    Mockito.when(verifier.verify(ID_TOKEN_STRING)).thenReturn(idToken);

    CreateUserServlet servlet = Mockito.spy(CreateUserServlet.class);
    Mockito.doNothing().when(servlet).createUser(GOOGLE_USER_ID.hashCode(), NAME, EMAIL);
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

    Mockito.verify(setupObj.response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}
