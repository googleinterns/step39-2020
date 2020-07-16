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
import { Grid, Typography, List, ListItem, ListItemText } from '@material-ui/core';
import { Map } from 'google-maps-react';
import './styles.css';

class StoreDetailCards extends Component {

    render() {
        const storeDetailCards = this.props.stores.map((store) => (
            <div>
              <Typography variant='h4'>{store.storeName}</Typography>
              <Typography variant='h6'>Address: {store.address}</Typography>
              <Grid container alignItems="stretch">
                <Grid xs>
                  <Map id="google-map" google={this.props.google} zoom={14}>
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
        return (
            <div>
              {storeDetailCards[0]}
            </div>
        )
    }
}

export default StoreDetailCards;