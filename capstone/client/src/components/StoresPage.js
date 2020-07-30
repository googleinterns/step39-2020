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
import { Redirect } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { Button, Card, CircularProgress, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, Grid } from '@material-ui/core';
import React, { Component } from 'react';

import { Store } from './Store';
import FilterStores from './FilterStores.js';
import StoreDetailCards from './StoreDetailCards.js';
import StoreOverviewCards from './StoreOverviewCards.js';
import { StoresProvider } from './StoresProvider.js';

import MailOutlineIcon from '@material-ui/icons/MailOutline';

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
      shareDialog: {
        display: false,
      },
      shareStatus: {
        display: false,
      },
      shareStatusMessage: null,
      email : null,
    }
    this.emailRegex = new RegExp("^[^@]+@[^@]+.[^@]+$");
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
          console.log(res.data);
          this.setState({
            stores: res.data,
            originalStores : res.data
          });
        });
    }
  goBack = () => {
    this.setState({
      redirect : `/lists/?latitude=${this.state.latitude}&longitude=${this.state.longitude}`,
    });
  }

  /* 
   * Displays a dialog prompting the user to specify a name for the list that 
   * is going to be saved. 
   */
  onShare = () => {
    this.setState({
      shareDialog: {
        display: true,
        shareButtonDisabled: true,
        error: true,
        errorText: "This is a required field."
      },
      errorMessage: null,
    });
  }

  /*
   * Checks to see if the "Email" field is empty. If the field is empty, an 
   * error message is displayed and the save button is disabled. 
   */
  onTextFieldChange = (event) => {
    if (!this.emailRegex.test(event.target.value)) {
      this.setState({
        shareDialog: {
          display: true,
          error: true,
          errorText: "This is a required field.",
          shareButtonDisabled : true,
        },
      });
    } else {
      this.setState({
        email: event.target.value,
        shareDialog: {
          display: true,
          shareButtonDisabled: false,
        },
      });
    }
  }

  /* 
   * Obtains the selected items from the checkbox list and makes a POST request to 
   * /api/v1/share-via-email to save the specified list.
   */
  handleDialogSubmit = () => {
    this.setState({
      shareDialog: {
        display: false,
      },
    });
    axios.post(
      '/api/v1/share-via-email',
      { 
        email : this.state.email,
        latitude : this.state.latitude,
        longitude : this.state.longitude,
        itemTypes : this.state.items,
        stores : this.state.originalStores,
      },
    ).then((res) => {
      this.setState({
        shareStatus: {
          display: true,
        },
        shareStatusMessage: "Your results have been shared!",
      });
    }).catch((error) => {
      this.setState({
        shareStatus: {
          display: true,
        },
        shareStatusMessage: "There was an error sharing your results.",
      })
    });
  }

  handleDialogCancel = () => {
    this.setState({
      shareDialog: {
        display: false,
      },
    })
  }

  handleShareStatusDialogClose = () => {
    this.setState({
      shareStatus: {
        display: false,
      },
    })
  }

  handleFilterChange = (stores) => {
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

    const overviewCards = (this.state.originalStores.length === 0) ? <CircularProgress id="stores-loading" color="action" /> : 
    <StoreOverviewCards stores={this.state.stores} numItems={this.state.items.length}/>;
    const shareButton = (this.state.originalStores.length === 0) ? null :
        (<Button id="back-button" variant="contained" onClick={this.onShare}>
           Share Results&nbsp;<MailOutlineIcon color='white' />
         </Button>)

    return(
      <div id="stores-page-container">
        <Helmet>
          <title> Stores | Shopsmart</title>
        </Helmet>
        <h1 className="stores-banner-text">Store Recommendations</h1>
        <StoresProvider>
        <Grid item>
            <Button id="back-button" onClick={this.goBack} variant="contained">Back To Items</Button>
            {shareButton}
        </Grid>
        <Grid item>
        <Dialog open={this.state.shareDialog.display} onClose={this.handleDialogCancel} aria-labelledby="form-dialog-title">
          <DialogTitle id="form-dialog-title">Share Results</DialogTitle>
          <DialogContent>
            <DialogContentText>
              To share your results, type an email below.
            </DialogContentText>
              <TextField
                autoFocus
                margin="dense"
                id="email"
                label="Email"
                helperText={this.state.shareDialog.errorText}
                error={this.state.shareDialog.error}
                onChange={this.onTextFieldChange}
                fullWidth
              />
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleDialogCancel} color="#77bce0">
              Cancel
            </Button>
            <Button disabled={this.state.shareDialog.shareButtonDisabled} onClick={this.handleDialogSubmit} color="primary">
              Share
            </Button>
          </DialogActions>
        </Dialog>
        <Dialog
          open={this.state.shareStatus.display}
          onClose={this.handleShareStatusDialogClose}
          aria-labelledby="form-dialog-title"
          aria-describedby="form-dialog-description">
          <DialogTitle id="share-dialog-title">{"Share Status"}</DialogTitle>
          <DialogContent>
          <DialogContentText id="share-dialog-text">
            {this.state.shareStatusMessage}
          </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleShareStatusDialogClose} color="primary">
              OK
            </Button>
          </DialogActions>
        </Dialog>
        </Grid>
        <Grid container alignItems="stretch">
          <Grid item component={Card} xs={4}>
            <FilterStores originalStores={this.state.originalStores} items={this.state.items} onFilterChange={this.handleFilterChange}/>
            <div id="overview-cards">{overviewCards}</div>
          </Grid>
          <Grid item component={Card} xs>
            <div id="details-cards">
            <StoreDetailCards stores={this.state.stores} style={{display: 'none'}}/>
            </div>
          </Grid>
        </Grid> 
        </StoresProvider>
      </div>
    );
  }
}

export const StorePageWithStore = Store.withStore(StorePage)