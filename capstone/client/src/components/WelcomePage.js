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

import React from 'react';
import { Redirect ,} from 'react-router-dom';

import { GoogleApiWrapper } from 'google-maps-react';
import { Button, Card, Grid, Typography } from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import APIKey from './APIKey.js';
import PlacesAutocomplete, {
  geocodeByAddress,
  getLatLng,
} from 'react-places-autocomplete';

import Footer from 'rc-footer';
import 'rc-footer/assets/index.css';
import Geocode from "react-geocode";

import { Store } from './Store';
import banner from './images/banner_no_text.png';
import login from './images/login.png';
import map from './images/map.png';
import checklist from './images/checklist.png';
import placeholder from './images/placehold.png';
import carolyn from './images/carolyn-profile.png';
import brett from './images/brett-profile.png';
import about from './images/grocery_graphic_small_circle.png';

import './styles.css';

class WelcomePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
        address: '21 North 5th Street, San Jose, CA 95112, USA', // Reverse geocoded from the default location
        location: { // San Jose by default
          latitude: 37.338207,
          longitude: -121.886330,
        },
        redirect: null,
    };
    Geocode.setApiKey(APIKey.APIKey());
  }

  handleChange = address => {
    // TODO(carolynlwang): Get full address from partial address and replace address field with it
    this.setState({ 
      address,
    });

    geocodeByAddress(address)
      .then(results => getLatLng(results[0]))
      .then(({lat, lng}) => {
        this.setState({
          location: {
            latitude: lat,
            longitude: lng,
          }
        });
      })
      .catch(error => {
        this.setState({
          location: {
            latitude: null,
            longitude: null,
          }
        });
      });
  }

  handleSelect = address => {
    this.setState({
      address,
    });
    geocodeByAddress(address)
      .then(results => getLatLng(results[0]))
      .then(({lat, lng}) => {
        this.setState({
          location: {
            latitude: lat,
            longitude: lng,
          }
        });
      })
      .catch(error => {
        this.setState({
          location: {
            latitude: null,
            longitude: null,
          }
        });
      });
  }

  onSubmit = async () => {
    var lat = this.state.location.latitude;
    var lng = this.state.location.longitude;

    if (lat && lng) {
      this.setState({
        redirect : `/lists/?latitude=${lat}&longitude=${lng}`,
      });
    }
  }

  requestLocation = () => {
    navigator.geolocation.getCurrentPosition((position) => {
      Geocode.fromLatLng(position.coords.latitude, position.coords.longitude).then(
        response => {
          const address = response.results[0].formatted_address;
          this.setState({
            address: address,
          });
        },
        error => {
          this.setState({
            address: null,
          });
        }
      );

      this.setState({
        location: {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        },
      });
    });
  }

  render() {
    if (this.state.redirect) {
      return <Redirect to={ this.state.redirect }/>
    }
    return (
      <div id="welcome-page-container">
        <div id="banner">
          <img id="banner-image" src={banner} alt="Banner"/>
          <div id="banner-text-container">
            <h1 className="banner-text">Shopsmart</h1>
          </div>
          <div id="slogan-text">
            <h2 className="slogan-text">Get prices you deserve.</h2>
          </div>
          <div id="location-input-container">
          <Grid container alignItems="stretch" id="location-input-grid">
            <PlacesAutocomplete
            value={this.state.address}
            onChange={this.handleChange}
            onSelect={this.handleSelect}
            >
              {({ getInputProps, suggestions, getSuggestionItemProps }) => (
                <div>
                  <input
                  {...getInputProps({
                  placeholder: 'Enter your address...',
                  className: 'location-input'
                  })}
                  />
                  {suggestions.map(suggestion => (
                  <div {...getSuggestionItemProps(suggestion)}>
                    <Button class="location-suggestion">{suggestion.description}</Button>
                  </div>
                  ))}
                  
                </div>
              )}
            </PlacesAutocomplete>
            <div><Button id="enter-location-button" onClick={this.onSubmit}><SearchIcon /></Button></div>
          </Grid>
          <div><Button id="current-location-button" onClick={this.requestLocation}>Use current location</Button></div>
          </div>
        </div>
        <Grid container justify="center" id="features-grid-container">
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={login} alt="login" class="feature-image"/>
              <h3 className="card-title">Create an account</h3>
              <Typography variant="body1" class="body-text">Log in with Google to retrieve and<br></br>save lists for all occasions. </Typography> 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={checklist} alt="checklist" class="feature-image"/>
              <h3 className="card-title">Add items to your list</h3>
              <Typography variant="body1" class="body-text">Create new lists with items<br></br>you're looking for. </Typography> 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={map} alt="map" class="feature-image"/>
              <h3 className="card-title">Find stores near you</h3>
              <Typography variant="body1" class="body-text">Choose from our recommendations, based on<br></br>price, distance, and item availability.</Typography> 
            </div>
          </Grid>
        </Grid> 
        <Grid container id="about-grid">
          <Grid item component={Card} xs>
            <div id="about-text">
              <h3 className="card-title">About Shopsmart</h3>
              <div id="about-body">
                <Typography variant="body1" class="body-text">When visiting retail stores, shoppers today face a variety of challenges. 
                While a shopper can use a simple Google Search to find the quantity and price for a single item, 
                this task becomes tedious when it comes to a list of multiple items, as with a shopping list. 
                The limitations and precautions taken to suppress the spread of the coronavirus have only exacerbated these difficulties. 
                For example, to reduce the risk of transmission, local and major grocery chains have begun to limit the number of people that can enter stores at any given time.  
                </Typography> 
                <br/>
                <Typography variant="body1" class="body-text">Now more than ever, people need efficient shopping experiences. 
                If we streamline this process of shopping for multiple items at once, a shopper will be better able to access the information they need before leaving the house.
                Shopsmart will help you save money and time by making more informed decisions about which stores to visit for your particular needs. 
</Typography>
              </div>
            </div>
          </Grid>
          <Grid item component={Card} xs> 
            <div id="about-image-card">
              <img src={about} alt="Woman pushing grocery cart" id="about-image"/> 
            </div>
          </Grid>
        </Grid>
        <Grid container justify="center" id="features-grid-container">
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="anudeep-profile"/>
              <h3 className="card-title">Anudeep Yakkala</h3>
              <Typography variant="body1" class="body-text">We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. </Typography>
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={brett} alt="brett-profile" class="profile-photos"/>
              <h3 className="card-title">Brett Allen</h3>
              <Typography variant="body1" class="body-text">Brett Allen is a rising junior at Massachusetts Institute of Technology, where he plays for the varisty men's volleyball team. He is a simple man who loves three things: backend development, The Office, and his one-eyed dog named Fergie.</Typography>
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={carolyn} alt="carolyn-profile" class="profile-photos"/>
              <h3 className="card-title">Carolyn Wang</h3>
              <Typography variant="body1" class="body-text">Carolyn Wang is a rising junior at Columbia University who loves color palette generators, Donna Tartt's "The Secret History," and polished design docs.</Typography>
            </div>
          </Grid>
        </Grid> 
        <Footer bottom='© 2020 Shopsmart' backgroundColor='#05386b'></Footer> 
      </div>
    );
  }
}

export const WelcomePageWithStore = GoogleApiWrapper({
    apiKey: (APIKey.APIKey())
  })(Store.withStore(WelcomePage))