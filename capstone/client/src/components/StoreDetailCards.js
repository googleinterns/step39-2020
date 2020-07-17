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
import { GoogleApiWrapper, Map } from 'google-maps-react';
import { Card, Grid, List, ListItem, ListItemText, Typography } from '@material-ui/core';

import APIKey from './APIKey.js';
import { StoresContext } from './StoresProvider.js';
import './styles.css';

class StoreDetailCards extends Component {
  render() {
    const maps = {
      height : "25%",
      width : "25%"
    }
    const storeDetailCards = this.props.stores.map((store) => (
      <div>
        <Typography variant='h4'>{store.storeName}</Typography>
        <Typography variant='h6'>Address: {store.storeAddress}</Typography>
        <Grid container alignItems="stretch">
          <Grid item component={Card} xs>
            <Map id="google-map" google={this.props.google} style={maps} zoom={14}>
            </Map>
          </Grid>
          <Grid item component={Card} xs>
            <Typography variant='subtitle1'>Has:</Typography>
            <List>
              {Object.keys(store.items).map((itemType, i) => (
                <div>{Object.keys(store.items[itemType]).map((index, i) => (
                  <ListItem key = {i}>
                  <ListItemText>
                    {store.items[itemType][index].itemName} (${store.items[itemType][index].itemPrice})
                  </ListItemText>
                </ListItem>
                ))}
                </div>
              ))}
            </List>
          </Grid>
        </Grid>
      </div>
    ));
      
    return (
        <StoresContext.Consumer> 
          {(context) => (
            <div>{storeDetailCards[context.state.storeIndex]}</div>
          )}
        </StoresContext.Consumer>
    )
  }
}

export default GoogleApiWrapper({
  apiKey: (APIKey.APIKey())
})(StoreDetailCards)