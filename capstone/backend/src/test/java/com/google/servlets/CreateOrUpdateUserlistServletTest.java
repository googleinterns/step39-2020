package com.google.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class CreateOrUpdateUserlistServletTest extends TestCase {
  private static final String CORRECT_REQUEST_STRING = 
      "{\"userId\":2,\"userList\":{\"listId\":3,\"displayName\":\"List Name\",\"itemTypes\":[\"Butter\",\"Juice\", \"Peanuts\"]}}";
  private static final String INCORRECT_REQUEST_STRING = "{list:\"bread\"}";

  public void testDoPostSucceed() throws ServletException, IOException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(CORRECT_REQUEST_STRING);

    CreateOrUpdateUserlistServlet servlet = Mockito.spy(CreateOrUpdateUserlistServlet.class);
    Mockito.doNothing().when(servlet).writeUserLists(2, 3, Arrays.asList("Butter", "Juice", "Peanuts"), "List Name");
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue("Is valid json format", ServletTestUtil.isValidJson(result));
    assertTrue("Should contain list items", result.contains("\"Butter\",\"Juice\",\"Peanuts\""));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    SetupObj setupObj = ServletTestUtil.setupMockDataPost(INCORRECT_REQUEST_STRING);

    CreateOrUpdateUserlistServlet servlet = new CreateOrUpdateUserlistServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
  }
}