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
}