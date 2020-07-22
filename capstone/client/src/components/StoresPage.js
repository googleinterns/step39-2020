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
import {Redirect} from 'react-router-dom';
import { Button, Card, CircularProgress, Grid } from '@material-ui/core';
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
      const params = new URLSearchParams(window.location.search);
      this.state = {
          originalStores: [],
          stores : [],
          items : params.getAll('items'),
          distanceValue : params.get('distanceValue'),
          latitude : params.get('latitude'),
          longitude : params.get('longitude'),
          method : params.get('method'),
          zipCode : params.get('zipCode'),
          redirect : null,
      }
      this.getStores = this.getStores.bind(this);
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
            stores: res.data,
            originalStores : res.data
          });
        });
    }

  handleFilterChange = (stores) => {
    goBack = () => {
      this.setState({
        redirect : "/",
      });
    }
    this.setState({
      stores,
    });
  }

  render() {
    if(this.state.redirect) {
      return <Redirect to={{
        pathname : this.state.redirect,
      }}/>
    }

    const overviewCards = (this.state.stores.length === 0) ? <CircularProgress id="stores-loading" color="action" /> : 
    <StoreOverviewCards stores={this.state.stores} numItems={this.state.items.length}/>;

  const method = (this.state.method === "location") ? <h5>Calculated from tracked location</h5> : <h5>Calculated from Zip Code: {this.state.zipCode}</h5>

    return(
      <div id="stores-page-container">
        <h1>Store Recommendations</h1>
        {method}
        <StoresProvider>
        <Button id="back-button" onClick={this.goBack} color="primary" variant="contained">Back To List</Button>
        <Grid container alignItems="stretch">
          <Grid item component={Card} xs>
            <FilterStores originalStores={this.state.originalStores} items={this.state.items} onFilterChange={this.handleFilterChange}/>
            {overviewCards}
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