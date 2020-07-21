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
import { Card, Grid } from '@material-ui/core';
import React, { Component } from 'react';

import { Store } from './Store';
import FilterStores from './FilterStores.js';
import StoreDetailCards from './StoreDetailCards.js';
import StoreOverviewCards from './StoreOverviewCards.js';
import { StoresProvider } from './StoresProvider.js';

import './styles.css';

class StorePage extends Component { 
    constructor(props) {
      super(props)
      this.state = {
          originalStores: [],
          stores : [],
          items : null,
          distanceValue : null,
          latitude : null,
          longitude : null,
      }
      this.getStores = this.getStores.bind(this);
    }

    componentWillMount = () => {
        this.setState((props) => ({
          items : this.props.store.state.items,
          distanceValue : this.props.store.state.distanceValue,
          latitude : this.props.store.state.latitude,
          longitude : this.props.store.state.longitude,
        }));
    }

    componentDidMount = () => {
      // Get Stores from database.
      this.getStores();
    }

    getStores = () => {
      axios.get('/api/v1/get-store-rankings', { params : { 'user-preferences' : {
        latitude : this.state.latitude,
        longitude : this.state.longitude,
        distancePreference : this.state.distanceValue,
        selectedItemTypes : this.state.items,
      } }})
        .then(res => {
          this.setState({
            stores: res.data
          });
        });
    }

  handleFilterchange = (stores) => {
    this.setState({
      stores,
    });
  }

  render() {
    return(
      <div>
        <h1>Store Recommendations</h1>
        <StoresProvider>
        <Grid container alignItems="stretch">
          <Grid item component={Card} xs>
            <FilterStores originalStores={this.state.originalStores} items={this.state.items} onFilterChange={this.handleFilterchange}/>
            <StoreOverviewCards stores={this.state.stores}/>
          </Grid>
          <Grid item component={Card} xs>
            <StoreDetailCards stores={this.state.stores} style={{display: 'none'}}/>
          </Grid>
        </Grid> 
        </StoresProvider>
      </div>
    );
  }
}

export const StorePageWithStore = Store.withStore(StorePage)