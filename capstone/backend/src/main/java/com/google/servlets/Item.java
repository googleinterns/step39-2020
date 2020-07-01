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

  private int itemId;
  
  private int itemPrice;
  
  private String itemName;

  public Item(int id, int price, int name) {
    itemId = id;
    itemPrice = price;
    itemName = name;
  }

  public int getId(){
    return itemId;
  }

  public int getPrice() {
    return itemPrice;
  }

  public String getName() {
    return itemName;
  }

}