package com.google.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/share-via-email")
public class ShareViaEmailServlet extends HttpServlet {

  private String emailRegEx = "^[^@]+@[^@]+.[^@]+$";

  private class RequestBody {
    private String email;
    private String html;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String reqString = ServletUtil.getRequestBody(request);
    RequestBody requestBody = requestValidator(reqString);
    Gson g = new Gson();

    if (requestBody == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request syntax.");
      return;
    }
    if (!requestBody.email.matches(emailRegEx)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email format.");
      return;
    }
    try {
      sendEmail(requestBody.email, requestBody.html);
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String sStackTrace = sw.toString();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, sStackTrace);
      return;
    }
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
  }

  public void sendEmail(String email, String html) throws Exception {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    // Create a default MimeMessage object.
    Message message = new MimeMessage(session);
    // Set From: header field of the header.
    message.setFrom(new InternetAddress("shopsmart@step39-2020.appspotmail.com", "Shopsmart"));
    // Set To: header field of the header.
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    // Set Subject: header field
    message.setSubject("Message From ShopSmart");
    // Now set the actual message
    message.setContent(html, "text/html");
    System.out.println("sending...");
    // Send message
    Transport.send(message);
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null || requestBody.email == "" || requestBody.html == "") {
      return null;
    }
    return requestBody;
  }
}
