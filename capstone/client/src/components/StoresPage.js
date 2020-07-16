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

import './styles.css';

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
    this.getStores = this.getStores.bind(this);
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

export const StorePageWithStore = Store.withStore(StorePage)