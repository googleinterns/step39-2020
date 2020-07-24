package com.google.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/share-via-mailjet")
public class ShareViaEmailServlet extends HttpServlet {

  private String emailRegEx = "^[^@]+@[^@]+.[^@]+$";

  private class RequestBody {
    private String email;
    private String html;
  }

  private class ResponseBody {
    private String email;

    public ResponseBody(String email) {
      this.email = email;
    }
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
    } catch (MessagingException e) {
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occured while sending email.");
      return;
    }
    ResponseBody responseBody = new ResponseBody(requestBody.email);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(g.toJson(responseBody));
  }

  public void sendEmail(String email, String html) throws MessagingException {
    // Recipient's email ID needs to be mentioned.
    String to = email;
    // Sender's email ID needs to be mentioned
    String from = "shopsmart.step@gmail.com";
    // Assuming you are sending email from through gmails smtp
    String host = "smtp.gmail.com";
    // Get system properties
    Properties properties = System.getProperties();
    // Setup mail server
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", "465");
    properties.put("mail.smtp.ssl.enable", "true");
    properties.put("mail.smtp.auth", "true");
    // Get the Session object.// and pass username and password
    Session session =
        Session.getInstance(
            properties,
            new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    GmailCredentials.getUsername(), GmailCredentials.getPassword());
              }
            });
    // Used to debug SMTP issues
    session.setDebug(true);
    try {
      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);
      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));
      // Set To: header field of the header.
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      // Set Subject: header field
      message.setSubject("Message From ShopSmart");
      // Now set the actual message
      message.setContent(html, "text/html");
      System.out.println("sending...");
      // Send message
      Transport.send(message);
    } catch (MessagingException mex) {
      throw mex;
    }
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
