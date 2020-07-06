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
          alert: null,
          googleButton: this.props.store.get('loggedIn') ? (
            <GoogleLogout
              clientId={CLIENT_ID}
              buttonText="Logout"
              onLogoutSuccess={this.logoutSuccess}
            />) : (
            <GoogleLogin
              clientId={CLIENT_ID}
              buttonText="Login"
              onSuccess={this.loginSuccess}
              onFailure={this.loginFailure}
              cookiePolicy={'single_host_origin'}
            />),    
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
          alert: null,
          googleButton: (<GoogleLogout
            clientId={CLIENT_ID}
            buttonText="Logout"
            onLogoutSuccess={this.logoutSuccess}
          />),
        });
      }).catch((error) => {
        this.setState({
          alert: (<Alert severity="error">{error.message}</Alert>)
        })
      });   
    }

    loginFailure = () => {
      this.setState({
        alert: <Alert severity="error">There was an error signing into your account. Please try again.</Alert>
      })
    }

    logoutSuccess = () => {
      this.props.store.set('loggedIn')(false);
      this.props.store.set('userId')(-1);
      this.setState({
        googleButton: (<GoogleLogin
          clientId={CLIENT_ID}
          buttonText="Login"
          onSuccess={this.loginSuccess}
          onFailure={this.loginFailure}
          cookiePolicy={'single_host_origin'}
        />),
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
                {this.state.googleButton}
              </Toolbar>
            </AppBar>
            {this.state.alert}
          </div>
        )
    }
}

export const HeaderWithStore = Store.withStore(Header);