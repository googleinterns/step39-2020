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

import React, { Component } from 'react';
import axios from 'axios';
import { Grid, Card, Typography, List, ListItem, ListItemText, Button } from '@material-ui/core';
import { Map, GoogleApiWrapper } from 'google-maps-react';

import { Store } from './Store';

const stores = [{
    name: 'Safeway',
    address: '11050 Bollinger Canyon Rd, San Ramon',
    totalItemsFound: 5,
    items: [{name: 'O Organics Organic Whole Milk with Vitamin D - 1 Gallon', price: 4.55}, 
      {name: 'Lucerne Milk Reduced Fat 2% Milkfat 1 Gallon - 128 Fl. Oz.', price: 2.55},
      {name: 'Silk Almondmilk Original Unsweetened - Half Gallon', price: 4.33}],
    distance: 3.1,
    price: 8.96
}, 
{
    name: 'Walmart',
    address: '9100 Alcosta Blvd, San Ramon',
    totalItemsFound: 2,
    items: [{name: 'O Organics Organic Whole Milk with Vitamin D - 1 Gallon', price: 4.55}, 
      {name: 'Lucerne Milk Reduced Fat 2% Milkfat 1 Gallon - 128 Fl. Oz.', price: 2.55},
      {name: 'Silk Almondmilk Original Unsweetened - Half Gallon', price: 1.44}],
    distance: 2.3,
    price: 16.45,
}
]

const itemList = [
    "Milk",
    "Bread"
]

class StorePage extends Component { 
    constructor(props) {
      super(props)
      this.state = {
          stores : []
      }
      this.getStores();
    }

    componentWillMount = () => {
        // Get Stores from database.
        this.getStores();
    }

    getStores = () => {
      axios.get('/api/v1/get-stores-with-item-types', { params : { item_types : itemList }})
        .then(res => {
          this.setState({
            stores: res.data
          });
        });
    }

    render() {

      const storeOverviewCards = this.state.stores.map((store) => (
          <div>
            <Typography variant="h6">{store.storeName}</Typography>
            <List>
            <ListItem>
                <ListItemText>
                Has: {store.totalItemsFound}/{itemList.length}
                </ListItemText>
            </ListItem>
            <ListItem>
                <ListItemText>
                Lowest Potential Price: ${store.lowestPotentialPrice}
                </ListItemText>
            </ListItem>
            <ListItem>
                <ListItemText>
                Distance: 5 miles
                </ListItemText>
            </ListItem>
            </List>
            <Button variant="contained" color="primary">
              Show more Information
            </Button>
          </div>
      ));


      const storeDetailCards = this.state.stores.map((store) => (
        <div>
          <Typography variant='h4'>{store.name}</Typography>
          <Typography variant='h6'>Address: {store.address}</Typography>
          <Grid container alignItems="stretch">
            <Grid xs>
              <Map google={this.props.google} zoom={14} style={{width: '25%', height: '25%'}}>
              </Map>
            </Grid>
            <Grid xs>
              <Typography variant='subtitle1'>Has:</Typography>
              <List>
              {Object.keys(store.items).map((itemType, item) => (
                <ListItem>
                  <ListItemText>
                    {item.itemName} (${item.itemPrice})
                  </ListItemText>
                </ListItem>
              ))}
              </List>
            </Grid>
          </Grid>
        </div>
      ));

      return(
        <div>
          <h1>Store Recommendations</h1>
          <Grid container alignItems="stretch">
            <Grid item component={Card} xs>
              {storeOverviewCards}
            </Grid>
            <Grid item component={Card} xs>
              {storeDetailCards[0]}
            </Grid>
          </Grid>
        </div>
      )
    }
}

export const StorePageWithStore = GoogleApiWrapper({
  apiKey: ('AIzaSyAmMqit2VT4XMp0W2imrYduF8WmhLDPQgk')
})(Store.withStore(StorePage))