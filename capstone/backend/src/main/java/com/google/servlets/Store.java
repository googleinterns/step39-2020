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

public class Store implements Comparable<Store> {

  private double totalPrice;

  private long storeId;

  private List<Item> items = new ArrayList<Item>();

  private String storeAddress;

  private String storeName;

  public Store(long storeId, String storeName, String storeAddress){
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeAddress = storeAddress;
  }

  public Store(Store otherStore, long itemId, double itemPrice, String itemName) {
    this.storeId = otherStore.storeId;
    this.storeName = otherStore.storeName;
    this.storeAddress = otherStore.storeAddress;
    for(Item item : otherStore.items){
      this.items.add(item);
    }
    this.addItem(itemId, itemPrice, itemName);
  }

  public void addItem(long itemId, double itemPrice, String itemName) {
    Item newItem = new Item(itemId, itemPrice, itemName);
    items.add(newItem);
    totalPrice += itemPrice;
  }

  public int getNumberOfItems() {
    return items.size();
  }

  public long getStoreId() {
    return storeId;
  }

  @Override
  public int compareTo(Store otherStore) {
    if (this.totalPrice < otherStore.totalPrice) {
      return -1;
    } else if (this.totalPrice == otherStore.totalPrice) {
      return 0;
    } else {
      return 1;
    }
  }

  public boolean equals(Store otherStore) {
    if(this.storeId == otherStore.storeId && this.storeName == otherStore.storeName &&
        this.storeAddress == otherStore.storeAddress && this.totalPrice == otherStore.totalPrice) {
      for(int i = 0; i < items.size(); i++){
        if(!this.items.get(i).equals(otherStore.items.get(i))){
          return false;
        }
      }
    }
    return true;
  }

}