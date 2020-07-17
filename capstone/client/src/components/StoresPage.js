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

import axios from 'axios';
import { Card, Grid} from '@material-ui/core';
import React, { Component } from 'react';

import { Store } from './Store';
import StoreDetailCards from './StoreDetailCards.js';
import StoreOverviewCards from './StoreOverviewCards.js';
import StoresProvider from './StoresProvider.js';

import './styles.css';

const itemList = [
    "Milk",
    "Bread"
]

const storesCode = [{ 
  storeName: 'Safeway', 
  address: '11050 Bollinger Canyon Rd, San Ramon', 
  totalItemsFound: 5, 
  items: [{itemName: 'O Organics Organic Whole Milk with Vitamin D - 1 Gallon', itemPrice: 4.55, itemType: 'milk'}, 
    {itemName: 'Lucerne Milk Reduced Fat 2% Milkfat 1 Gallon - 128 Fl. Oz.', itemPrice: 2.55, itemType: 'milk'}, 
    {itemName: 'Silk Almondmilk Original Unsweetened - Half Gallon', itemPrice: 4.33, itemType: 'milk'}], 
  distance: 3.1, price: 8.96}, 
  { storeName: 'Walmart', 
  address: '9100 Alcosta Blvd, San Ramon', 
  totalItemsFound: 2, 
  items: [{itemName: 'O Organics Organic Whole Milk with Vitamin D - 1 Gallon', itemPrice: 4.55, itemType: 'milk'}, 
    {itemName: 'Lucerne Milk Reduced Fat 2% Milkfat 1 Gallon - 128 Fl. Oz.', itemPrice: 2.55, itemType: 'milk'}, 
    {itemName: 'Silk Almondmilk Original Unsweetened - Half Gallon', itemPrice: 1.44, itemType: 'milk'}], 
  distance: 2.3, price: 16.45,}]

class StorePage extends Component { 
  constructor(props) {
    super(props)
    this.state = {
      stores : []
    }
  }

  componentDidMount = () => {
    this.getStores = this.getStores.bind(this);
    // Get Stores from database.
    this.getStores();
  }

  getStores = () => {
    /* axios.get('/api/v1/get-stores-with-item-types', { params : { item_types : itemList }})
      .then(res => {
        this.setState({
          stores: res.data
        });
      });*/
    this.setState({
      stores: storesCode
    });
  }

  render() {
    return(
      <div>
        <h1>Store Recommendations</h1>
        <StoresProvider>
          
              <StoreOverviewCards stores={this.state.stores}/>
          
              <StoreDetailCards stores={this.state.stores} style={{display: 'none'}}/>
           
        </StoresProvider>
      </div>
    )
  }
}

export const StorePageWithStore = Store.withStore(StorePage)