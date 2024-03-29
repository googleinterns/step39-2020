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
import { AppBar, Button, Toolbar, Typography } from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import axios from 'axios';
import { GoogleLogin, GoogleLogout } from 'react-google-login';
import { Redirect, withRouter } from "react-router-dom";

import './styles.css';
import logo from './images/logo.png';
import { Store } from './Store';

const CLIENT_ID = "618901837293-lggv074jcmas0qvt2gvatjsb62r219om.apps.googleusercontent.com";

class Header extends Component {
    constructor(props) {
      super(props);
      this.state = {
          errorMessage: null,
          loggedIn: this.props.store.get('loggedIn'), 
          redirect: null,
      }
    }
    /*
     * Obtains the id_token from the authResponse and makes a POST request to 
     * /api/v1/create-user and saves the userId from the response in the store. 
     */
    loginSuccess = (response) => {
      axios.post(
        '/api/v1/create-user',
        { 
          idTokenString: response.getAuthResponse().id_token,
        },
      ).then((res) => {
        this.props.store.set('userId')(res.data.userId);
        this.props.store.set('loggedIn')(true);
        this.setState({
          errorMessage: null,
          loggedIn: true,
        });
      }).catch((error) => {
        this.setState({
          errorMessage: "There was an error signing into your account. Please try again."
        })
      });   
    }

    loginFailure = () => {
      this.setState({
        errorMessage: "There was an error signing into your account. Please try again."
      })
    }

    /* 
     * Changes the value of loggedIn to false and resets the userId. Any error messages
     * are also cleared. 
     */
    logoutSuccess = () => {
      this.props.store.set('loggedIn')(false);
      this.props.store.set('userId')(-1);
      this.setState({
        errorMessage: null,
        loggedIn: false,
      });
    }

    redirectToHome = () => {
      this.setState({
        redirect: '/'
      });
    }

    render() {
      // Change the color of the header depending on what page we're on
      let headerColor = 'transparent';
      if (this.props.page === 'welcome') {
        headerColor = '#77bce0';
      } else if (this.props.page === 'lists') {
        headerColor = '#ebebed';
      } else if (this.props.page === 'stores') {
        headerColor = '#d5dfe1';
      }

      const headerStyle = {
        background: headerColor
      }

      // Disable hover effect for the logo
      const buttonStyle = {
        backgroundColor: 'transparent'
      }

      if (this.state.redirect) {
        if (this.props.page === 'welcome') {
          window.location.reload(true);
        }
        else {
          return <Redirect to={{
            pathname : '/'
          }}/>
        }
      }
        return (
          <div>
            <AppBar position="static" style={headerStyle}>
              <Toolbar>
                <Typography id="typography" variant="h6">
                  <Button id="shopsmart-logo" onClick={this.redirectToHome} style={buttonStyle}>
                    <img src={logo} alt="Shopsmart logo" id="shopsmart-logo"/>Shopsmart
                  </Button>
                </Typography>
                {this.state.loggedIn ? 
                <GoogleLogout
                  display="none"
                  clientId={CLIENT_ID}
                  buttonText="Log out"
                  onLogoutSuccess={this.logoutSuccess}
                /> :
                <GoogleLogin
                  clientId={CLIENT_ID}
                  buttonText="Sign in with Google"
                  onSuccess={this.loginSuccess}
                  onFailure={this.loginFailure}
                  cookiePolicy={'single_host_origin'}
                  theme="dark"
                  isSignedIn={true}
                />}
              </Toolbar>
            </AppBar>
            {this.state.errorMessage ? <Alert severity="error">{this.state.errorMessage}</Alert> : null}
          </div>
        )
    }
}

export const HeaderWithStore = withRouter(Store.withStore(Header));