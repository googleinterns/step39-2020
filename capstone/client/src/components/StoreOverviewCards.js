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
import { Typography, List, ListItem, ListItemText, Button } from '@material-ui/core';

import { StoresContext } from './StoresProvider.js';
import './styles.css';

class StoreOverviewCards extends Component {

    render() {
        const storeOverviewCards = this.props.stores.map((store, index) => (
            <div>
              <Typography variant="h6">{store.storeName}</Typography>
              <List>
              <ListItem>
                  <ListItemText>
                  Has: {store.totalItemsFound}/6
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
              <StoresContext.Consumer>
                  {(context) => (
                    <Button variant="contained" color="primary" onClick={() => 
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