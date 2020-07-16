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
import { Button, Card, Checkbox, Dialog, DialogActions,DialogContent, DialogContentText, 
  DialogTitle, FormGroup, FormControlLabel, Grid, List, ListItem, ListItemText,  
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
      items: [],
      listId: -1,
      listName: null,
      userId: -1,
      displayZipCodeInput: false,
      location: null,
      listSaveDialog: {
        display: false,
      },
    } 
  }

  componentWillMount = () => {
    // Get ItemTypes from database.
    this.getItemTypes();
    this.selectedItems = new Set();
    this.props.store.on('userId').subscribe((userId) => {
      this.setState({
        userId,
      });
    }
    );
    
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
    if (arr.length === 0) {
      this.setState({
        selectedItemsList: null,
        errorMessage: "Please select at least one item!",
      });
      return;
    }
    const listItems = arr.map((item) => (
      <ListItem key={item}>
        <ListItemText
          primary={item}
          data-testid='list item'
          />
      </ListItem>
    ));
    this.setState({
      selectedItemsList: listItems,
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

  render() {
    const checkboxItems = this.state.items.map((item) => (
      <FormControlLabel
        control={<Checkbox name={item} data-testid='checkbox item'/>}
        label={item}
        key={item}
        onChange={this.handleItemChange}
        />
    ));

    const distances = [2, 4, 6, 8, 10, 12, 14].map((item) => (
      <FormControlLabel
        control={<Radio name={item + " mile radius"}/>}
        label={item + " mile radius"}
        value={item}
        key={item}
        onChange={this.handleDistanceChange}
        />
    ));

    return (
      <div id="list-page-container">
        {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
        {this.state.successMessage ? <Alert severity="success">{this.state.successMessage}</Alert> : null}
        <h1>Preferences</h1>
        <Grid container alignItems="stretch">
          <Grid id="distance-list-container" item component={Card} xs>
            <p>I would like to choose from stores in a</p>
            <RadioGroup id="distance-list" value={this.state.distanceValue}>
              {distances} 
            </RadioGroup>
          </Grid>
          <Grid id="items-list-container" item component={Card} xs>
            <p>Select items to add to your list</p>
            <FormGroup id="items-list">
              {checkboxItems}
            </FormGroup>
            <Button onClick={this.onSave} color="secondary" variant="contained">Save List</Button>
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