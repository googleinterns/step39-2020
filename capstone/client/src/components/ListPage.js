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
import {Redirect} from 'react-router-dom';
import axios from 'axios';
import { Button, ButtonGroup, Card, FormControlLabel, Grid, List, Radio, TextField } from '@material-ui/core';
import { Alert } from '@material-ui/lab';

import { Store } from './Store';

import Geocode from "react-geocode";
import APIKey from './APIKey.js';
import ItemsList from './ItemsList.js';

const MAX_JAVA_INTEGER = 2147483647;

/*
 * Displays a checkbox list containing the items returned from the Items API. 
 * The selected items are displayed below when the form is submitted. 
 */
class ListPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedItemsList: null,
      errorMessage: null,
      successMessage: null,
      distanceValue: MAX_JAVA_INTEGER,
      totalLists: 0,
      userLists : [],
      items: [],
      selectedItems: new Set(),
      listId: -1,
      listName: null,
      userId: -1,
      location: { // San Jose by default
        latitude: 37.338207,
        longitude: -121.886330,
      },
      redirect : null,
    }
    Geocode.setApiKey(APIKey.APIKey());
  }

  componentWillMount = () => {
    // Get ItemTypes from database.
    this.getItemTypes();
    this.itemsToComponent = {};
    this.props.store.on('userId').subscribe((userId) => {
      this.setState({
        userId,
      });
    });
  
    navigator.geolocation.getCurrentPosition((position) => {
      this.setState({
        location: {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        },
      });
    }, () => {
      this.setState({
        zipCodeRequired: true,
      });
    });
    if(this.state.userId !== -1) {
      axios.get('/api/v1/get-user-lists', { params : { userId : this.state.userId }})
        .then(res => {
          this.setState({
            totalLists: res.data.userLists.length,
            userLists: res.data.userLists
          });
        });
    }
  }

  /* 
   * Displays a list of the items selected from the checkbox list. 
   * @TODO Change this function to make a GET request to obtain store recommendations based 
   * on the selected items.
   */
   onSubmit = async (selectedItems) => {
    var latit = this.state.location.latitude;
    var longi = this.state.location.longitude;
    var method = "location";
   
    const arr = Array.from(selectedItems);
    this.props.store.set('items')(arr);
    this.props.store.set('latitude')(this.state.location.latitude);
    this.props.store.set('longitude')(this.state.location.longitude);
    
    let redirectAddress = '/stores/?';
    for(let i = 0; i < arr.length; i++){
      redirectAddress = redirectAddress + `items=${arr[i]}&`;
    }
    redirectAddress = redirectAddress + `latitude=${latit}&longitude=${longi}&distanceValue=${MAX_JAVA_INTEGER}&method=${method}`;
    this.setState({
      redirect : redirectAddress,
      errorMessage: null,
    });
  }

  getItemTypes = () => {
    axios.get('/api/v1/get-item-types' 
    ).then((res) => {
      this.setState({
        items: res.data,
      })
    }).catch((error) => {
      this.setState({
        errorMessage: "There was an error retrieving items.",
      })
    }); 
  }

  selectList = (event) => {
    var index = event.target.name;
    if(event.target.className === "MuiButton-label"){
      index = event.target.parentElement.name;
    }
    this.setState({
      listId : this.state.userLists[index].listId,
      selectedItems: new Set(this.state.userLists[index].itemTypes),
    });
  }

  render() {
    if(this.state.redirect) {
      return <Redirect to={{
        pathname : this.state.redirect,
      }}/>
    }

    if(this.state.userId !== -1 && this.state.userLists.length === 0) {
      axios.get('/api/v1/get-user-lists', { params : { userId : this.state.userId }})
        .then(res => {
          this.setState({
            totalLists: res.data.userLists.length,
            userLists: res.data.userLists
          });
        });
    }

    const userListButtons = this.state.userLists.map((userList, index) => (
      <Button id="list-button" name={index}>{userList.displayName}</Button>
    ));

    return (
      <div id="list-page-container">
        {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
        {this.state.successMessage ? <Alert severity="success">{this.state.successMessage}</Alert> : null}
        <h1>Item Selection</h1>
        <ButtonGroup container id="user-lists" onClick={this.selectList}>
          {userListButtons}
        </ButtonGroup>
        <Grid container alignItems="stretch">
          <Grid id="items-list-container" item component={Card} xs>
            {<ItemsList items={this.state.items} selectedItems={this.state.selectedItems} userId={this.state.userId} listId={this.state.listId} onSubmit={this.onSubmit}/>}
          </Grid>
        </Grid> 
      </div>
    );
  }
}

export const ListPageWithStore = Store.withStore(ListPage);