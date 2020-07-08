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

import java.util.ArrayList;
import java.util.List;

public class Store {
  private float totalPrice;

  private long storeId;

  private List<Item> items = new ArrayList<Item>();

  private String storeAddress;

  private String storeName;

  public Store(long storeId, String storeName, String storeAddress) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeAddress = storeAddress;
  }

  public Store(Store otherStore, long itemId, float itemPrice, String itemName) {
    this.storeId = otherStore.storeId;
    this.storeName = otherStore.storeName;
    this.storeAddress = otherStore.storeAddress;
    this.addItem(itemId, itemPrice, itemName);
  }

  public void addItem(long itemId, float itemPrice, String itemName) {
    Item newItem = new Item(itemId, itemPrice, itemName);
    items.add(newItem);
    totalPrice += itemPrice;
  }

  public int getNumberOfItems() {
    return items.size();
  }
}