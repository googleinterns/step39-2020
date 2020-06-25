package com.google.sps.servlets;

import com.google.gson.Gson; 
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.Date;
import com.google.cloud.spanner.DatabaseClient;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

enum ResponseStatus {
    SUCCESS,
    BAD_REQUEST,
    REQUEST_TIMELIMIT_EXCEEDED,
    INTERNAL_SERVER_ERROR
}

/**
* 
*/
@WebServlet("/api/v1/create-or-update-user-list-servlet")
public class CreateOrUpdateUserlistServlet extends HttpServlet {
  private String DATABASE_INSTANCE = "Capstone Instance";
  private String DATABASE_NAME = "step39-db";

  private class ResponseBody {
    private ResponseStatus responseStatus;

    public ResponseBody(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
  }

  private class RequestBody {
    private int userId;
    private UserList userList;
  }

  private class UserList {
    private int listId;
    private String displayName;
    private List<String> itemTypes;
  }
 
 @Override
 public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
   Gson g = new Gson();
   RequestBody requestBody = getRequestBody(request);
   DatabaseClient dbClient = initClient();
   writeData(requestBody, dbClient);

   ResponseBody responseBody = new ResponseBody(ResponseStatus.SUCCESS);
   response.setContentType("application/json;");
   response.getWriter().println(g.toJson(responseBody));
 }
  
  private void writeData(RequestBody requestBody, DatabaseClient dbClient) {
    List<Mutation> mutations = Arrays.asList(
      Mutation.newInsertOrUpdateBuilder("UserLists")
          .set("UserId")
          .to(requestBody.userId)
          .set("ListId")
          .to(requestBody.userList.listId)
          .set("ItemTypes")
          .toStringArray(requestBody.userList.itemTypes)
          .build());
    dbClient.write(mutations);
  }
  
  private DatabaseClient initClient() {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();

    DatabaseId db = DatabaseId.of(options.getProjectId(), DATABASE_INSTANCE, DATABASE_NAME);
    return spanner.getDatabaseClient(db);
  }

  private RequestBody getRequestBody(HttpServletRequest request) throws IOException {
    Gson g = new Gson();
    BufferedReader br = request.getReader();
    String reqString = "";
    String line;

    while((line = br.readLine()) != null) {
      reqString += line;
    }
    return g.fromJson(reqString, RequestBody.class);
  }
}