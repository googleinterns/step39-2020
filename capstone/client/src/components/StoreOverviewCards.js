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

class StoreOverviewCards extends Component {

    render() {
        const storeOverviewCards = this.props.stores.map((store, index) => (
            <div id="store-overview-card">
              <Typography variant="h6" component="h6">{store.storeName}</Typography>
              <List>
              <ListItem>
                  <ListItemIcon>
                    <ShoppingCartIcon color='primary' />
                  </ListItemIcon>
                  <ListItemText>
                  Total Items Found: {store.totalItemsFound}/{this.props.numItems}
                  </ListItemText>
              </ListItem>
              <ListItem>
                  <ListItemIcon>
                    <AttachMoneyIcon color='primary' />
                  </ListItemIcon>
                  <ListItemText>
                  Lowest Potential Price: ${(store.lowestPotentialPrice-.005).toFixed(2)}
                  </ListItemText>
              </ListItem>
              <ListItem>
                  <ListItemIcon>
                    <DriveEtaIcon color='primary' />
                  </ListItemIcon>
                  <ListItemText>
                  Distance: {store.distance}
                  </ListItemText>
              </ListItem>
              </List>
              <StoresContext.Consumer>
                  {(context) => (
                    <Button id="show-more-info" variant="contained" color="primary" onClick={() => 
                      context.setStore(index)}>
                    Show more Information
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