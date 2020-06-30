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