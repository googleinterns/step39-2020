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

public class Item {
  private String itemId;

  private long storeId;

  private double itemPrice;
  
  private String itemBrand;
  
  private String itemName;

  public Item(String itemId, double itemPrice, String itemName, String itemBrand, long storeId) {
    this.itemId = itemId;
    this.itemPrice = itemPrice;
    this.itemName = itemName;
    this.itemBrand = itemBrand;
    this.storeId = storeId;
  }

  public String getId() {
    return itemId;
  }

  public double getPrice() {
    return itemPrice;
  }

  public String getName() {
    return itemName;
  }

  public String getBrand() {
    return itemBrand;
  }

  public boolean equals(Item otherItem) {
    return this.itemId.equals(otherItem.itemId) && this.itemPrice == otherItem.itemPrice && 
            this.itemName.equals(otherItem.itemName) && this.storeId == otherItem.storeId && this.itemBrand.equals(otherItem.itemBrand);
  }
}
