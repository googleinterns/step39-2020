import React, { Component } from 'react';
import { AppBar, Toolbar, IconButton, Typography } from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import axios from 'axios';
import { Menu } from '@material-ui/icons';
import { GoogleLogin, GoogleLogout } from 'react-google-login';

import { Store } from './Store';

const CLIENT_ID = "618901837293-lggv074jcmas0qvt2gvatjsb62r219om.apps.googleusercontent.com";

class Header extends Component {
    constructor(props) {
      super(props);
      this.state = {
          errorMessage: null,
          loggedIn: this.props.store.get('loggedIn'), 
      }
    }

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

    logoutSuccess = () => {
      this.props.store.set('loggedIn')(false);
      this.props.store.set('userId')(-1);
      this.setState({
        errorMessage: null,
        loggedIn: false,
      });
    }

    render() {
        return (
          <div>
            <AppBar position="static">
              <Toolbar>
                <IconButton edge="start" color="inherit" aria-label="menu">
                  <Menu />
                </IconButton>
                <Typography id="typography" variant="h6">
                  Preferences
                </Typography>
                {this.state.loggedIn ? 
                <GoogleLogout
                  display="none"
                  clientId={CLIENT_ID}
                  buttonText="Logout"
                  onLogoutSuccess={this.logoutSuccess}
                /> :
                <GoogleLogin
                  clientId={CLIENT_ID}
                  buttonText="Login"
                  onSuccess={this.loginSuccess}
                  onFailure={this.loginFailure}
                  cookiePolicy={'single_host_origin'}
                />}
              </Toolbar>
            </AppBar>
            <Alert severity="error">{this.state.errorMessage}</Alert>
          </div>
        )
    }
}

export const HeaderWithStore = Store.withStore(Header);