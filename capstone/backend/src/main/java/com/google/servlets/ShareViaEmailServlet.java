package com.google.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
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
    private double latitude;
    private double longitude;
    private List<String> itemTypes;
    private List<Store> stores;
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
      sendEmail(
          requestBody.email,
          getStoresHTML(
              requestBody.stores,
              requestBody.itemTypes,
              requestBody.latitude,
              requestBody.longitude));
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

  public void sendEmail(String email, String storesHTML) throws Exception {
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
    message.setContent(storesHTML, "text/html");
    System.out.println("sending...");
    // Send message
    Transport.send(message);
  }

  private String getStoresHTML(
      List<Store> stores, List<String> itemTypes, double latitude, double longitude) {
    String url = "https://step39-2020.uc.r.appspot.com/stores/?";
    String items = itemTypes.get(0);
    boolean first = true;
    for (String item : itemTypes) {
      url = url + "items=" + item + "&";
      items = items + ", " + item;
    }
    url =
        url
            + "latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&distanceValue=2147483647&method=location";
    String storesString = "";
    for (Store store : stores) {
      storesString =
          storesString
              + "<h3>"
              + store.getStoreName()
              + "</h3>"
              + "<p>Total Items Found: "
              + store.getNumberOfItemsFound()
              + "/"
              + itemTypes.size()
              + "</p>"
              + "<p>Lowest Potential Price: "
              + store.getLowestPotentialPrice()
              + "</p>"
              + "<p>Distance: "
              + store.getDistanceFromUser()
              + "</p>";
    }
    String html =
        "<div style='text-align:center;'>"
            + "<h1 text-align='center'>Hello from Shopsmart!</h1>"
            + "<h3>Someone wants to share their results with you!</h3>"
            + "<a target='_blank' href='"
            + url
            + "'>See the results for yourself!<a>"
            + "</div>"
            + "<p>"
            + "<b>Items</b>"
            + "</p>"
            + "<p>&nbsp;&nbsp;"
            + items
            + "</p>"
            + storesString;
    return html;
  }

  private RequestBody requestValidator(String reqString) {
    Gson g = new Gson();
    RequestBody requestBody = g.fromJson(reqString, RequestBody.class);
    if (requestBody == null
        || requestBody.email == ""
        || requestBody.latitude == 0
        || requestBody.longitude == 0
        || requestBody.itemTypes == null
        || requestBody.stores == null) {
      return null;
    }
    return requestBody;
  }
}
