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
import { Grid, Card} from '@material-ui/core';
import { GoogleApiWrapper } from 'google-maps-react';

import { Store } from './Store';
import APIKey from './APIKey.js';
import StoreOverviewCards from './StoreOverviewCards.js';
import StoreDetailCards from './StoreDetailCards.js';
import './styles.css';


class StorePage extends Component { 
    constructor(props) {
      super(props)
      this.state = {
          stores : [],
          items : null,
      }
      this.getStores = this.getStores.bind(this);
    }

    componentWillMount = () => {
        // Get Stores from database.
        this.getStores();
        this.setState((props) => ({
          items : this.props.store.state.items
        }))
    }

    getStores = () => {
      axios.get('/api/v1/get-stores-with-item-types', { params : { item_types : this.state.items }})
        .then(res => {
          this.setState({
            stores: res.data
          });
        });
    }

    render() {

      return(
        <div>
          <h1>Store Recommendations</h1>
          <Grid container alignItems="stretch">
            <Grid item component={Card} xs>
              <StoreOverviewCards stores={this.state.stores}/>
            </Grid>
            <Grid item component={Card} xs>
              <StoreDetailCards stores={this.state.stores}/>
            </Grid>
          </Grid>
        </div>
      )
    }
}

export const StorePageWithStore = GoogleApiWrapper({
  apiKey: (APIKey.APIKey())
})(Store.withStore(StorePage))