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

import java.util.List;

public class UserList {
  private long listId;
  private String displayName;
  private List<String> itemTypes;

  public UserList(long listId, String displayName, List<String> itemTypes) {
    this.listId = listId;
    this.displayName = displayName;
    this.itemTypes = itemTypes;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof UserList)) {
      return false;
    }
    UserList userList = (UserList) obj;
    if (userList.listId != this.listId) {
      return false;
    }
    if (!userList.displayName.equals(this.displayName)) {
      return false;
    }
    if (!userList.itemTypes.equals(this.itemTypes)) {
      return false;
    }
    return true;
  }
}