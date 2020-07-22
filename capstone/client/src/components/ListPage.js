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
import { Button, ButtonGroup, Card, Checkbox, CircularProgress, Dialog, DialogActions,DialogContent, DialogContentText, 
  DialogTitle, FormGroup, FormControlLabel, Grid, List,
  Radio, RadioGroup, TextField } 
  from '@material-ui/core';
import { Alert } from '@material-ui/lab';

import { Store } from './Store';

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
      distanceValue: 4,
      totalLists: 0,
      userLists : [],
      items: [],
      listId: -1,
      listName: null,
      userId: -1,
      displayZipCodeInput: false,
      location: { // San Jose by default
        latitude: 37.338207,
        longitude: -121.886330,
      },
      listSaveDialog: {
        display: false,
      },
      redirect : null,
    }
  }

  componentWillMount = () => {
    // Get ItemTypes from database.
    this.getItemTypes();
    this.selectedItems = new Set();
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
        displayZipCodeInput: true,
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
   * Adds an item to the selectedItems list if an item is checked and removes
   * an item if it is unchecked. 
   */
  handleItemChange = (event) => {
    if (event.target.checked) {
      this.selectedItems.add(event.target.name);
    } else {
      this.selectedItems.delete(event.target.name);
    }
  }

  /* 
   * Saves the most recently selected distance preference. 
   */
  handleDistanceChange = (event) => {
    this.setState({
      distanceValue: parseInt(event.target.value),
    })
  }

  /* 
   * Displays a list of the items selected from the checkbox list. 
   * @TODO Change this function to make a GET request to obtain store recommendations based 
   * on the selected items.
   */
  onSubmit = () => {
    const arr = [...this.selectedItems];
    this.props.store.set('items')(arr);
    this.props.store.set('latitude')(this.state.location.latitude);
    this.props.store.set('longitude')(this.state.location.longitude);
    this.props.store.set('distanceValue')(this.state.distanceValue);
    if (arr.length === 0) {
      this.setState({
        selectedItemsList: null,
        errorMessage: "Please select at least one item!",
      });
      return;
    }
    let redirectAddress = '/stores/?';
    for(let i = 0; i < arr.length; i++){
      redirectAddress = redirectAddress + `items=${arr[i]}&`;
    }
    redirectAddress = redirectAddress + `latitude=${this.state.location.latitude}&longitude=${this.state.location.longitude}&distanceValue=${this.state.distanceValue}`;
    this.setState({
      redirect : redirectAddress,
      errorMessage: null,
    });
  }


  /* 
   * Displays a dialog prompting the user to specify a name for the list that 
   * is going to be saved. 
   */
  onSave = () => {
    const arr = [...this.selectedItems];
    if (arr.length === 0) {
      this.setState({
        errorMessage: "Please select at least one item!",
      });
    } else {
      this.setState({
        listSaveDialog: {
          display: true,
          saveButtonDisabled: true,
          error: true,
          errorText: "This is a required field."
        },
        errorMessage: null,
      });
    }
  }

  handleDialogCancel = () => {
    this.setState({
      listSaveDialog: {
        display: false,
      },
    })
  }

  /* 
   * Obtains the selected items from the checkbox list and makes a POST request to 
   * /api/v1/create-or-update-user-list-servlet to save the specified list.
   */
  handleDialogSubmit = () => {
    this.setState({
      listSaveDialog: {
        display: false,
      },
    });
    const arr = [...this.selectedItems];
    axios.post(
      '/api/v1/create-or-update-user-list-servlet',
      { 
        userId: this.state.userId,
        userList: {
          listId: this.state.listId,
          displayName: this.state.listName,
          itemTypes: arr
        }
      },
    ).then((res) => {
      this.setState({
        errorMessage: null,
        successMessage: "Your list has been saved!",
        listId: res.data.userList.listId,
      });
    }).catch((error) => {
      this.setState({
        errorMessage: "There was an error saving your list.",
      })
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

  /*
   * Checks to see if the "List Name" field is empty. If the field is empty, an 
   * error message is displayed and the save button is disabled. 
   */
  onTextFieldChange = (event) => {
    if (event.target.value.trim() === '') {
      this.setState({
        listSaveDialog: {
          display: true,
          error: true,
          errorText: "This is a required field.",
          saveButton: true,
        },
      });
    } else {
      this.setState({
        listName: event.target.value,
        listSaveDialog: {
          display: true,
          saveButtonDisabled: false,
        },
      });
    }
  }

  selectList = (event) => {
    if (this.selectedItems.size !== 0) {
      for (let val of this.selectedItems) {
        this.itemsToComponent[val].click();
      }
    }
    var index = event.target.name;
    if(event.target.className === "MuiButton-label"){
      index = event.target.parentElement.name;
    }
    this.setState({
      listId : this.state.userLists[index].listId,
    });
    for (const i in this.state.userLists[index].itemTypes) {
      this.itemsToComponent[this.state.userLists[index].itemTypes[i]].click();
    }
  }

  render() {
    if(this.state.redirect) {
      return <Redirect to={{
        pathname : this.state.redirect,
      }}/>
    }

    if(this.state.userId !== -1 && this.state.userLists === []) {
      axios.get('/api/v1/get-user-lists', { params : { userId : this.state.userId }})
        .then(res => {
          this.setState({
            totalLists: res.data.userLists.length,
            userLists: res.data.userLists
          });
        });
    }

    const userListButtons = this.state.userLists.map((userList, index) => (
    <Button name={index}>{userList.displayName}</Button>
    ));

    const checkboxItems = (this.state.items.length === 0) ? <CircularProgress id="loading-spinner" color="action" /> : this.state.items.map((item) => (
      <FormControlLabel
        control={<Checkbox name={item} ref={component => this.itemsToComponent[item] = component} data-testid='checkbox item'/>}
        label={item}
        key={item}
        onChange={this.handleItemChange}
        />
    ));

    const distances = [2, 4, 6, 8, 10, 12, 14].map((item) => (
      <FormControlLabel
        control={<Radio name={item + " miles from current location"}/>}
        label={item + " miles from current location"}
        value={item}
        key={item}
        onChange={this.handleDistanceChange}
        />
    ));

    const saveButton = (this.state.userId === -1) ? <div></div> : <Button onClick={this.onSave} color="secondary" variant="contained">Save List</Button>;

    return (
      <div id="list-page-container">
        {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
        {this.state.successMessage ? <Alert severity="success">{this.state.successMessage}</Alert> : null}
        <h1>Preferences</h1>
        <ButtonGroup container id="user-lists" onClick={this.selectList}>
          {userListButtons}
        </ButtonGroup>
        <Grid container alignItems="stretch">
          <Grid id="distance-list-container" item component={Card} xs>
            <p>Select a distance preference</p>
            <RadioGroup id="distance-list" value={this.state.distanceValue}>
              {distances}
              <FormControlLabel
                control={<Radio name={"None"}/>}
                label={"None"}
                value={Number.MAX_SAFE_INTEGER}
                key={"None"}
                onChange={this.handleDistanceChange}
               />
            </RadioGroup>
          </Grid>
          <Grid id="items-list-container" item component={Card} xs>
            <p>Select items to add to your list</p>
            <FormGroup id="items-list">
              {checkboxItems}
            </FormGroup>
            {saveButton}
            <List>
              {this.state.selectedItemsList}
            </List>
          </Grid>
        </Grid>
        {this.state.displayZipCodeInput ? <TextField display={this.state.location} id="filled-basic" label="Zip Code" variant="filled" /> : null}
        <br></br>
        <br></br>
        <Button id="submit-button" onClick={this.onSubmit} color="primary" variant="contained">Find Stores</Button>
        <Dialog open={this.state.listSaveDialog.display} onClose={this.handleDialogCancel} aria-labelledby="form-dialog-title">
          <DialogTitle id="form-dialog-title">Save List</DialogTitle>
          <DialogContent>
            <DialogContentText>
              To save a list containing the selected items to your account, please enter a list name. 
            </DialogContentText>
              <TextField
                autoFocus
                margin="dense"
                id="list-name"
                label="List Name"
                helperText={this.state.listSaveDialog.errorText}
                error={this.state.listSaveDialog.error}
                onChange={this.onTextFieldChange}
                fullWidth
              />
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleDialogCancel} color="primary">
              Cancel
            </Button>
            <Button disabled={this.state.listSaveDialog.saveButtonDisabled} onClick={this.handleDialogSubmit} color="primary">
              Save
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    );
  }
}

export const ListPageWithStore = Store.withStore(ListPage);