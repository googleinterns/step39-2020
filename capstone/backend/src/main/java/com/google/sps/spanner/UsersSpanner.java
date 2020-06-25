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


package com.example.spanner;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.BackupId;
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


public class UsersSpanner {
  static void createDatabase(DatabaseAdminClient dbAdminClient, DatabaseId id) {
    OperationFuture<Database, CreateDatabaseMetadata> op =
        dbAdminClient.createDatabase(
            id.getInstanceId().getInstance(),
            id.getDatabase(),
            Arrays.asList(
                "CREATE TABLE Users ("
                    + "  UserId   INT64,"
                    + "  Username  STRING(MAX),"
                    + "  Email   STRING(MAX)"
                    + ") PRIMARY KEY (UserId)",
                "CREATE TABLE UserLists ("
                    + "  UserId     INT64,"
                    + "  ListId      INT64,"
                    + "  ItemTypes   ARRAY<STRING(MAX)>"
                    + ") PRIMARY KEY (UserId, ListId),"
                    + "  INTERLEAVE IN PARENT Users ON DELETE CASCADE",
                "CREATE TABLE Stores ("
                    + "StoreId      INT64,"
                    + "StoreName    STRING(MAX),"
                    + "Address     STRING(MAX)"
                    + ") PRIMARY KEY (StoreId)",
                "CREATE TABLE Items ("
                    + "ItemId      INT64,"
                    + "ItemNameAndBrand    STRING(MAX),"
                    + "ItemType     STRING(MAX)"
                    + ") PRIMARY KEY (ItemId)",
                "CREATE TABLE Inventory ("
                    + "StoreId     INT64,"
                    + "ItemId      INT64,"
                    + "ItemAvailability STRING(MAX),"
                    + "LastUpdated TIMESTAMP OPTIONS (allow_commit_timestamp=true),"
                    + "Price FLOAT64,"
                    + "CONSTRAINT FK_ItemId FOREIGN KEY (ItemId) REFERENCES Items (ItemId)"
                    + ") PRIMARY KEY (StoreId, ItemId),"
                    + "INTERLEAVE IN PARENT Stores ON DELETE CASCADE"));
    try {
      // Initiate the request which returns an OperationFuture.
      Database db = op.get();
      System.out.println("Created database [" + db.getId() + "]");
    } catch (ExecutionException e) {
      // If the operation failed during execution, expose the cause.
      throw (SpannerException) e.getCause();
    } catch (InterruptedException e) {
      // Throw when a thread is waiting, sleeping, or otherwise occupied,
      // and the thread is interrupted, either before or during the activity.
      throw SpannerExceptionFactory.propagateInterrupt(e);
    }
  }
  
  static void run(
      DatabaseClient dbClient,
      DatabaseAdminClient dbAdminClient,
      InstanceAdminClient instanceAdminClient,
      String command,
      DatabaseId database,
      BackupId backup) {
    switch (command) {
      case "createdatabase":
        createDatabase(dbAdminClient, database);
        break;
      default:
        printUsageAndExit();
    }
  }
  
  static void printUsageAndExit() {
    System.err.println("Usage:");
    System.err.println("    SpannerExample <command> <instance_id> <database_id>");
    System.err.println("");
    System.err.println("Examples:");
    System.err.println("    SpannerExample createdatabase my-instance step39-db");
    System.err.println("    SpannerExample write my-instance step39-db");
    System.err.println("    SpannerExample delete my-instance step39-db");
    System.err.println("    SpannerExample query my-instance step39-db");
    System.err.println("    SpannerExample read my-instance step39-db");
    System.err.println("    SpannerExample addmarketingbudget my-instance step39-db");
    System.err.println("    SpannerExample update my-instance step39-db");
    System.err.println("    SpannerExample writetransaction my-instance step39-db");
    System.err.println("    SpannerExample querymarketingbudget my-instance step39-db");
    System.err.println("    SpannerExample addindex my-instance step39-db");
    System.err.println("    SpannerExample readindex my-instance step39-db");
    System.err.println("    SpannerExample queryindex my-instance step39-db");
    System.err.println("    SpannerExample addstoringindex my-instance step39-db");
    System.err.println("    SpannerExample readstoringindex my-instance step39-db");
    System.err.println("    SpannerExample readonlytransaction my-instance step39-db");
    System.err.println("    SpannerExample readstaledata my-instance step39-db");
    System.err.println("    SpannerExample addcommittimestamp my-instance step39-db");
    System.err.println("    SpannerExample updatewithtimestamp my-instance step39-db");
    System.err.println("    SpannerExample querywithtimestamp my-instance step39-db");
    System.err.println("    SpannerExample createtablewithtimestamp my-instance step39-db");
    System.err.println("    SpannerExample writewithtimestamp my-instance step39-db");
    System.err.println("    SpannerExample querysingerstable my-instance step39-db");
    System.err.println("    SpannerExample queryperformancestable my-instance step39-db");
    System.err.println("    SpannerExample writestructdata my-instance step39-db");
    System.err.println("    SpannerExample querywithstruct my-instance step39-db");
    System.err.println("    SpannerExample querywitharrayofstruct my-instance step39-db");
    System.err.println("    SpannerExample querystructfield my-instance step39-db");
    System.err.println("    SpannerExample querynestedstructfield my-instance step39-db");
    System.err.println("    SpannerExample insertusingdml my-instance step39-db");
    System.err.println("    SpannerExample updateusingdml my-instance step39-db");
    System.err.println("    SpannerExample deleteusingdml my-instance step39-db");
    System.err.println("    SpannerExample updateusingdmlwithtimestamp my-instance step39-db");
    System.err.println("    SpannerExample writeandreadusingdml my-instance step39-db");
    System.err.println("    SpannerExample updateusingdmlwithstruct my-instance step39-db");
    System.err.println("    SpannerExample writeusingdml my-instance step39-db");
    System.err.println("    SpannerExample querywithparameter my-instance step39-db");
    System.err.println("    SpannerExample writewithtransactionusingdml my-instance step39-db");
    System.err.println("    SpannerExample updateusingpartitioneddml my-instance step39-db");
    System.err.println("    SpannerExample deleteusingpartitioneddml my-instance step39-db");
    System.err.println("    SpannerExample updateusingbatchdml my-instance step39-db");
    System.err.println("    SpannerExample createtablewithdatatypes my-instance step39-db");
    System.err.println("    SpannerExample writedatatypesdata my-instance step39-db");
    System.err.println("    SpannerExample querywitharray my-instance step39-db");
    System.err.println("    SpannerExample querywithbool my-instance step39-db");
    System.err.println("    SpannerExample querywithbytes my-instance step39-db");
    System.err.println("    SpannerExample querywithdate my-instance step39-db");
    System.err.println("    SpannerExample querywithfloat my-instance step39-db");
    System.err.println("    SpannerExample querywithint my-instance step39-db");
    System.err.println("    SpannerExample querywithstring my-instance step39-db");
    System.err.println("    SpannerExample querywithtimestampparameter my-instance step39-db");
    System.err.println("    SpannerExample clientwithqueryoptions my-instance step39-db");
    System.err.println("    SpannerExample querywithqueryoptions my-instance step39-db");
    System.err.println("    SpannerExample createbackup my-instance step39-db");
    System.err.println("    SpannerExample listbackups my-instance step39-db");
    System.err.println("    SpannerExample listbackupoperations my-instance step39-db");
    System.err.println("    SpannerExample listdatabaseoperations my-instance step39-db");
    System.err.println("    SpannerExample restorebackup my-instance step39-db");
    System.exit(1);
  }
  
  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      printUsageAndExit();
    }
    // [START init_client]
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    try {
      String command = args[0];
      DatabaseId db = DatabaseId.of(options.getProjectId(), args[1], args[2]);
      // [END init_client]
      // This will return the default project id based on the environment.
      String clientProject = spanner.getOptions().getProjectId();
      if (!db.getInstanceId().getProject().equals(clientProject)) {
        System.err.println(
            "Invalid project specified. Project in the database id should match the"
                + "project name set in the environment variable GOOGLE_CLOUD_PROJECT. Expected: "
                + clientProject);
        printUsageAndExit();
      }
      // Generate a backup id for the sample database.
      String backupName =
          String.format(
              "%s_%02d",
              db.getDatabase(), LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR));
      BackupId backup = BackupId.of(db.getInstanceId(), backupName);

      // [START init_client]
      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
      InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();
      // Use client here...
      // [END init_client]
      run(dbClient, dbAdminClient, instanceAdminClient, command, db, backup);
    } finally {
      spanner.close();
    }
    // [END init_client]
    System.out.println("Closed client");
  }
}
