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

  private int totalPrice = 0;

  private int storeId;

  private List<Item> items = new ArrayList<Item>();

  private String storeAddress;

  private String storeName;

  public Store(int id, String name, String address){
    storeId = id;
    storeName = name;
    storeAddress = address;
  }

  public Store(StoreResult otherStore, String itemId, int priceOfItem, String itemName) {
    this.storeId = otherStore.storeId;
    this.storeName = otherStore.storeName;
    this.storeAddress = otherStore.storeAddress;
    this.addItem(itemId, priceOfItem, itemName);
  }

  public void addItem(int itemId, int priceOfItem, String itemName) {
    Item newItem = new Item(itemId, priceOfItem, itemName)
    items.add(newItem);
    totalPrice += priceOfItem;
  }

  public int getNumberOfItems() {
    return items.size();
  }

}