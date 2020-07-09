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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store implements Comparable<Store> {

  private double lowestPotentialPrice;

  private int totalItemsFound;

  private long storeId;

  private Map<String, List<Item>> items = new HashMap<String, List<Item>>();

  private Map<String, Double> typeToPrice = new HashMap<String, Double>();

  private String storeAddress;

  private String storeName;

  public Store(long storeId, String storeName, String storeAddress) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeAddress = storeAddress;
  }

  public void addItem(String itemId, double itemPrice, String itemName, String itemBrand, String itemType) {
    Item newItem = new Item(itemId, itemPrice, itemName, itemBrand, this.storeId);
    if(items.containsKey(itemType)){
      items.get(itemType).add(newItem);
      if (itemPrice < typeToPrice.get(itemType)) {
        typeToPrice.put(itemType, itemPrice);
        lowestPotentialPrice += itemPrice;
      }
    } else {
      totalItemsFound++;
      items.put(itemType, new ArrayList<Item>(Arrays.asList(newItem)));
      typeToPrice.put(itemType, itemPrice);
      lowestPotentialPrice += itemPrice;
    }
  }

  public int getNumberOfItemsFound() {
    return totalItemsFound;
  }

  public long getStoreId() {
    return storeId;
  }

  @Override
  public int compareTo(Store otherStore) {
    if (this.lowestPotentialPrice < otherStore.lowestPotentialPrice) {
      return -1;
    } else if (this.lowestPotentialPrice == otherStore.lowestPotentialPrice) {
      return 0;
    } else {
      return 1;
    }
  }

  public boolean equals(Store otherStore) {
    if (this.storeId == otherStore.storeId
        && this.storeName == otherStore.storeName
        && this.storeAddress == otherStore.storeAddress
        && this.lowestPotentialPrice == otherStore.lowestPotentialPrice) {
      for (String key : items.keySet()) {
        for (int i = 0; i < items.get(i).size(); i++) {
          if (!this.items.get(key).get(i).equals(otherStore.items.get(key).get(i))) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
