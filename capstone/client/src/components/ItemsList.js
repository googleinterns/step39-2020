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
import { Avatar, Button, Chip, CircularProgress, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, Typography } from '@material-ui/core';
import { CheckCircle } from '@material-ui/icons';
import { Alert } from '@material-ui/lab';

import cerealImage from './images/cereal.jpg';
import chipsImage from './images/chips.jpg';
import cookiesImage from './images/cookies.jpg';
import flourImage from './images/flour.jpg';
import ketchupImage from './images/ketchup.jpg';
import milkImage from './images/milk.jpg';
import pencilImage from './images/pencil.jpg';
import ramenImage from './images/ramen.jpg';
import shampooImage from './images/shampoo.jpg';
import sodaImage from './images/soda.jpg';
import sugarImage from './images/sugar.jpg';
import waterImage from './images/water.jpg';
import napkinImage from './images/napkin.jpg'
import paperTowelsImage from './images/paper towels.jpg';
import oliveOilImage from './images/olive oil.jpg';

import './styles.css';

const images = {
  cereal: cerealImage,
  chips: chipsImage,
  cookies: cookiesImage,
  flour: flourImage,
  ketchup: ketchupImage,
  milk: milkImage,
  pencil: pencilImage,
  ramen: ramenImage,
  shampoo: shampooImage,
  soda: sodaImage,
  sugar: sugarImage,
  water: waterImage,
  napkin: napkinImage,
  'olive oil': oliveOilImage,
  'paper towels': paperTowelsImage,
}

class ItemsList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedItems: new Set(),
      listSaveDialog: {
        display: false,
      },
      listSaveStatus: {
        display: false,
      },
      listSaveStatusMessage: null,
      errorMessage: null,
      listName: null,
      listId: null,
    }
  }

  /* 
   * Obtains the selected items from the checkbox list and makes a POST request to 
   * /api/v1/create-or-update-user-list-servlet to save the specified list.
   */
  handleDialogSubmit = (successMessage, errorMessage, listName) => {
    const displayName = (listName === null) ? this.state.listName : listName;
    this.setState({
      listSaveDialog: {
        display: false,
      },
    });
    axios.post(
      '/api/v1/create-or-update-user-list-servlet',
      { 
        userId: this.props.userId,
        userList: {
          listId: this.props.listId,
          displayName,
          itemTypes: Array.from(this.state.selectedItems),
        }
      },
    ).then((res) => {
      this.setState({
        listSaveStatus: {
          display: true,
        },
        listSaveStatusMessage: successMessage,
        listId: res.data.userList.listId,
      });
      this.props.onSave(res.data.userList.listId);
    }).catch((error) => {
      this.setState({
        listSaveStatus: {
          display: true,
        },
        listSaveStatusMessage: errorMessage,
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

  /* 
   * Displays a dialog prompting the user to specify a name for the list that 
   * is going to be saved. 
   */
  onSave = () => {
    const arr = [...this.state.selectedItems];
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

  onUpdate = () => {
    const arr = [...this.state.selectedItems];
    if (arr.length === 0) {
      this.setState({
        errorMessage: "Please select at least one item!",
      });
    }
    this.handleDialogSubmit("Your list has been updated.", "There was an error updating your list.", this.props.listName)
  }

  handleDialogCancel = () => {
    this.setState({
      listSaveDialog: {
        display: false,
      },
    })
  }

  handleListStatusDialogClose = () => {
    this.setState({
      listSaveStatus: {
        display: false,
      },
    });
  }

  onItemAdd = (item) => {
    const selectedItems = this.state.selectedItems;
    selectedItems.add(item);
    this.setState({
      selectedItems,
    });
  }

  onItemDelete = (item) => {
    const selectedItems = this.state.selectedItems;
    selectedItems.delete(item);
    this.setState({
      selectedItems,
    });
  }

  onSubmit = () => {
    if (this.state.selectedItems.size === 0) {
      this.setState({
        errorMessage: "Please select at least one item!",
      });
      return;
    }
    this.props.onSubmit(this.state.selectedItems);
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    if (prevState.selectedItems !== nextProps.selectedItems) {
      return {
        selectedItems: nextProps.selectedItems,
      };
    }
  }
  
  render() {
    const checkboxItems = (this.props.items.length === 0) ? <CircularProgress id="loading-spinner" color="action" /> : this.props.items.map((item) => (
        this.state.selectedItems.has(item) ? (
          <Chip
            id="selected-item"
            avatar={<Avatar id="item-icon" src={images[item]}/>}
            clickable
            deleteIcon={<CheckCircle id="check-icon"/>}
            onDelete={() => {this.onItemDelete(item)}}
            onClick={() => {this.onItemDelete(item)}}
        label={<Typography variant="h6">{item}</Typography>}
            key={item}
        />) : (
          <Chip
            id="item"
            avatar={<Avatar id="item-icon" src={images[item]}/>}
            clickable
            onClick={() => {this.onItemAdd(item)}}
            label={<Typography variant="h6">{item}</Typography>}
            key={item}
          />)));
    
    const saveButton = (this.props.userId === -1) ? null : 
      ((this.props.listId !== -1) ? 
        <Button id="selection-button" variant="contained" color="primary" onClick={this.onUpdate}>Update List</Button> : 
        <Button id="selection-button" variant="contained" color="primary" onClick={this.onSave}>Save List</Button>)

    const saveDirections = (this.props.userId !== -1) ? " If you want to retrieve your lists later, save your list. " :
      " If you want to save your list for later, log in with Google. ";

    return(
      <div>
        <div id="items-container">
          {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
          <div id="item-list-text">
            <Typography variant="h3">Select items</Typography>
            <Typography id="directions-text" variant="h6">
              Click on the items you want to add to the current list. <br/>
              {saveDirections}<br/>
              When you're done, select "Find Stores" to get your store recommendations.
            </Typography>
          </div>
          {checkboxItems}
        <Typography id="number-selected-text" variant="h6">You have selected <b>{this.state.selectedItems.size}</b> items</Typography>
          {saveButton}
          <Button id="selection-button" variant="contained" color="primary" onClick={this.onSubmit}>
            Find Stores
          </Button>
        </div>
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
            <Button 
              disabled={this.state.listSaveDialog.saveButtonDisabled} 
              onClick={() => { this.handleDialogSubmit("Your list has been saved.", "There was an error saving yout list.", null) }} color="primary">
              Save
            </Button>
          </DialogActions>
        </Dialog>
        <Dialog
          open={this.state.listSaveStatus.display}
          onClose={this.handleListStatusDialogClose}
          aria-labelledby="form-dialog-title"
          aria-describedby="form-dialog-description">
          <DialogTitle id="list-save-dialog-title">{"List Status"}</DialogTitle>
          <DialogContent>
          <DialogContentText id="list-save-dialog-text">
            {this.state.listSaveStatusMessage}
          </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleListStatusDialogClose} color="primary">
              OK
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    )
  }
}

export default ItemsList;