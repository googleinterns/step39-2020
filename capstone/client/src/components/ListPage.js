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
import { Button, Card, Grid, InputLabel, MenuItem, Select } from '@material-ui/core';
import { Add, Create, Delete } from '@material-ui/icons';
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
    const params = new URLSearchParams(window.location.search);
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
      listIndex: -1,
      listName: null,
      userId: this.props.store.get('userId'),
      location: { 
        latitude: params.get('latitude'),
        longitude: params.get('longitude'),
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
  
    if (this.state.userId !== -1) {
      axios.get('/api/v1/get-user-lists', { params : { userId : this.state.userId }})
        .then(res => {
          this.setState({
            totalLists: res.data.userLists.length,
            userLists: res.data.userLists
          });
        });
    }
  }

  getUserLists = (listId) => {
    axios.get('/api/v1/get-user-lists', { params : { userId : this.state.userId }})
        .then(res => {
          let listIndex = -1;
          for (let i = 0; i < res.data.userLists.length; i++) {
            if (listId === res.data.userLists[i].listId) {
              listIndex = i;
            }
          }
          this.setState({
            totalLists: res.data.userLists.length,
            userLists: res.data.userLists,
            listId,
            listIndex,
          });
        });
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
    const index = event.target.value;
    if (index === undefined) {
      return;
    }
    if (index === -1) {
      this.setState({
        listId: -1,
        listIndex: -1,
        selectedItems: new Set(),
        listName: null,
      });
      return;
    }
    this.setState({
      listId : this.state.userLists[index].listId,
      listIndex: index,
      selectedItems: new Set(this.state.userLists[index].itemTypes),
      listName: this.state.userLists[index].displayName,
    });
  }

  removeList = () => {
    axios.get('/api/v1/remove-user-list', { params: { userId: this.state.userId, listId: this.state.listId }})
      .then(() => {
        this.setState({
          successMessage: "This list has been successfully deleted."
        });
      }).catch(() => {
        this.setState({
          errorMessage: "There was an error deleting this list."
        });
      });
      window.location.reload();
  }

  goBack = () => {
    this.setState({
      redirect : "/",
    });
  }

  render() {
    if (this.state.redirect) {
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
      <MenuItem id="list-button" value={index} key={index}> 
        <Create fontSize="inherit"/><span> </span>{userList.displayName}
      </MenuItem>
    ));

    return (
      <div id="list-page-container">
        {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
        {this.state.successMessage ? <Alert severity="success">{this.state.successMessage}</Alert> : null}
        <h1>Item Selection</h1>
        <div id="back-button-container"><Button id="back-button-list-page" onClick={this.goBack}>Back to home</Button></div>
        {this.state.userId === -1 ? null : 
          <div id="list-selection-buttons-container">
            <InputLabel>Select from saved lists</InputLabel>
            {(this.state.userId !== -1 && this.state.listId !== -1) ? <Button onClick={this.removeList}><div id="delete-icon-container"><Delete /></div></Button> : null}
            <Select variant="standard" value={this.state.listIndex} container id="user-lists" onClick={this.selectList}>
              <MenuItem id="list-button" value={-1} key={-1}>
                <Add fontSize="inherit"/><span> </span>New list
              </MenuItem>
              {userListButtons}
            </Select>
          </div>
        }
        <Grid container alignItems="stretch">
          <Grid id="items-list-container" item component={Card} xs>
            {<ItemsList 
              items={this.state.items} 
              selectedItems={this.state.selectedItems} 
              userId={this.state.userId} 
              listId={this.state.listId} 
              listName={this.state.listName} 
              onSubmit={this.onSubmit} 
              onSave={this.getUserLists} />}
          </Grid>
        </Grid> 
      </div>
    );
  }
}

export const ListPageWithStore = Store.withStore(ListPage);