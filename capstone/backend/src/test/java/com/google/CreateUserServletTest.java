package com.google;

import com.google.gson.Gson;
import com.google.servlets.CreateUserServlet;
import org.mockito.Mockito;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;


public class CreateUserServletTest extends TestCase {
  private static final String CORRECT_REQUEST_STRING = 
      "{\"email\": \"test@gmail.com\",\"userName\":\"test user\"}";
  private static final String INCORRECT_REQUEST_STRING = "{\"bad response\" list:\"bread\"}";

  private class SetupObj {
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter writer;

    public SetupObj(HttpServletRequest request, HttpServletResponse response, 
        StringWriter writer) {
      this.request = request;
      this.response = response;
      this.writer = writer;
    }
  }

  public void testDoPostSucceed() throws ServletException, IOException {
    SetupObj setupObj = setupMockData(CORRECT_REQUEST_STRING);

    CreateUserServlet servlet = new CreateUserServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_OK);
    String result = setupObj.writer.toString();
    assertTrue(isValidJson(result));
    assertTrue("Should contain userid in object", result.contains("userId:"));
  }

  public void testDoPostBadRequest() throws ServletException, IOException {
    SetupObj setupObj = setupMockData(INCORRECT_REQUEST_STRING);

    CreateUserServlet servlet = new CreateUserServlet();
    servlet.doPost(setupObj.request, setupObj.response);

    Mockito.verify(setupObj.response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    String result = setupObj.writer.toString();
    assertTrue("Should say invalid syntax", result.contains("Invalid request syntax."));
  }

  public SetupObj setupMockData(String reqString) throws IOException {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
    StringWriter writer = new StringWriter();
    StringReader reader = new StringReader(reqString);
    Mockito.when(req.getMethod()).thenReturn("POST");
    Mockito.when(req.getReader()).thenReturn(new BufferedReader(reader));
    Mockito.when(res.getWriter()).thenReturn(new PrintWriter(writer));
    return new SetupObj(req, res, writer);
  }

  private static boolean isValidJson(String str) {
    Gson g = new Gson();
    try {
      g.fromJson(str, Object.class);
      return true;
    } catch(com.google.gson.JsonSyntaxException e) { 
      return false;
    }
  }
} 