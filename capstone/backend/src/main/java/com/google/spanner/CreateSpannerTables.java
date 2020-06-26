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


package com.java.spanner;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.InstanceAdminClient;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerExceptionFactory;
import com.google.cloud.spanner.SpannerOptions;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoField;


public class CreateSpannerTables {

  /*
   * Creates database using an Admin Client, accessing the instance signalled in
   * the DatabaseId and creating 5 hard coded tables: Users, UserLists, Stores, Items,
   * and Inventory.
   * 
   * @param dbAdminClient client used to access the instance and database
   * @param id contains the information regarding the instance (name, etc.)
   *
   * @throws SpannerException if failed during execution
   */
  static void createDatabase(DatabaseAdminClient dbAdminClient, DatabaseId id) {
    OperationFuture<Database, CreateDatabaseMetadata> op =
        dbAdminClient.createDatabase(
            id.getInstanceId().getInstance(),
            id.getDatabase(),
            Arrays.asList(
                "CREATE TABLE Users ("
                    + "  UserId    INT64,"
                    + "  Username  STRING(MAX),"
                    + "  Email     STRING(MAX)"
                    + ") PRIMARY KEY (UserId)",
                "CREATE TABLE UserLists ("
                    + "  UserId      INT64,"
                    + "  ListId      INT64,"
                    + "  ItemTypes   ARRAY<STRING(MAX)>"
                    + ") PRIMARY KEY (UserId, ListId),"
                    + "  INTERLEAVE IN PARENT Users ON DELETE CASCADE",
                "CREATE TABLE Stores ("
                    + "  StoreId      INT64,"
                    + "  StoreName    STRING(MAX),"
                    + "  Address      STRING(MAX)"
                    + ") PRIMARY KEY (StoreId)",
                "CREATE TABLE Items ("
                    + "  ItemId            INT64,"
                    + "  ItemNameAndBrand  STRING(MAX),"
                    + "  ItemType          STRING(MAX)"
                    + ") PRIMARY KEY (ItemId)",
                "CREATE TABLE Inventories ("
                    + "  StoreId           INT64,"
                    + "  ItemId            INT64,"
                    + "  ItemAvailability  STRING(MAX),"
                    + "  LastUpdated       TIMESTAMP OPTIONS (allow_commit_timestamp=true),"
                    + "  Price             FLOAT64,"
                    + "  CONSTRAINT FK_ItemId FOREIGN KEY (ItemId) REFERENCES Items (ItemId)"
                    + ") PRIMARY KEY (StoreId, ItemId),"
                    + "INTERLEAVE IN PARENT Stores ON DELETE CASCADE"));
    try {
      // Initiate the request which returns an OperationFuture.
      Database db = op.get();
    } catch (ExecutionException e) {
      // If the operation failed during execution, expose the cause.
      throw (SpannerException) e.getCause();
    } catch (InterruptedException e) {
      // Throw when a thread is waiting, sleeping, or otherwise occupied,
      // and the thread is interrupted, either before or during the activity.
      throw SpannerExceptionFactory.propagateInterrupt(e);
    }
  }
  
  private static void run(
      DatabaseClient dbClient,
      DatabaseAdminClient dbAdminClient,
      InstanceAdminClient instanceAdminClient,
      String command,
      DatabaseId database) {
    switch (command) {
      case "createdatabase":
        createDatabase(dbAdminClient, database);
        break;
      default:
        printUsageAndExit();
    }
  }
  
  private static void printUsageAndExit() {
    System.err.println("Usage:");
    System.err.println("    SpannerExample <command> <instance_id> <database_id>");
    System.err.println("");
    System.err.println("Examples:");
    System.err.println("    SpannerExample createdatabase my-instance step39-db");
    System.exit(1);
  }
  
  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      printUsageAndExit();
    }
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    try {
      String command = args[0];
      DatabaseId db = DatabaseId.of(options.getProjectId(), args[1], args[2]);
      // This will return the default project id based on the environment.
      String clientProject = spanner.getOptions().getProjectId();
      if (!db.getInstanceId().getProject().equals(clientProject)) {
        System.err.println(
            "Invalid project specified. Project in the database id should match the"
                + "project name set in the environment variable GOOGLE_CLOUD_PROJECT. Expected: "
                + clientProject);
        printUsageAndExit();
      }

      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
      InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();
      run(dbClient, dbAdminClient, instanceAdminClient, command, db);
    } finally {
      spanner.close();
    }
    System.out.println("Closed client");
  }
}
