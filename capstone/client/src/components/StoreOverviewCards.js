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
import { Typography, List, ListItem, ListItemIcon, ListItemText, Button } from '@material-ui/core';
import ShoppingCartIcon from '@material-ui/icons/ShoppingCart';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';
import DriveEtaIcon from '@material-ui/icons/DriveEta';

import { StoresContext } from './StoresProvider.js';
import './styles.css';

import safeway from './images/safeway-logo.png';
import walmart from './images/walmart-logo.jpg';
import target from './images/target-logo.png';

const logos = {
  "S" : safeway,
  "W" : walmart,
  "T" : target
}

class StoreOverviewCards extends Component {
  render() {
    const storeOverviewCards = this.props.stores.map((store, index) => (
      <div id="store-overview-card">
        <img id="store-logo" src={logos[store.storeName[0]]} alt={store.storeName[0]}/>
        <Typography variant="h6" component="h6">{store.storeName}</Typography>
        <List>
          <ListItem>
            <ListItemIcon>
              <ShoppingCartIcon id="store-icon" aria-label="Shopping cart"/>
            </ListItemIcon>
            <ListItemText>
              Total Items Found: {store.totalItemsFound}/{this.props.numItems}
            </ListItemText>
          </ListItem>
          <ListItem>
            <ListItemIcon>
              <AttachMoneyIcon id="store-icon" aria-label="Dollar sign"/>
            </ListItemIcon>
            <ListItemText>
              Lowest Potential Price: ${(store.lowestPotentialPrice-.005).toFixed(2)}
            </ListItemText>
          </ListItem>
          <ListItem>
            <ListItemIcon>
              <DriveEtaIcon id="store-icon" aria-label="Car"/>
            </ListItemIcon>
            <ListItemText>
              Distance: {store.distanceFromUser.toFixed(1)} miles
            </ListItemText>
          </ListItem>
        </List>
        <StoresContext.Consumer>
          {(context) => (
            <Button variant="contained" id="interior-button" onClick={() => 
              context.setStore(index)}>
              Show More Information
            </Button>
          )}
        </StoresContext.Consumer>
      </div>
    ));
    
    return (
      <div>
        {storeOverviewCards}
      </div>
    )
  }
}

export default StoreOverviewCards;