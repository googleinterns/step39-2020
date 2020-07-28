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
import { GoogleApiWrapper } from 'google-maps-react';
import { Button, Card, Grid, Typography } from '@material-ui/core';
import APIKey from './APIKey.js';
import PlacesAutocomplete, {
  geocodeByAddress,
  getLatLng,
} from 'react-places-autocomplete';
import Footer from 'rc-footer';
import 'rc-footer/assets/index.css';

import banner from './images/banner_no_text.png';
import placeholder from './images/placehold.png';

import './styles.css';

class WelcomePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
        address: '' 
    };
  }

  handleChange = address => {
    this.setState({ 
      address,
    });
  };

  handleSelect = address => {
    this.setState({
      address,
    });
    geocodeByAddress(address)
      .then(results => getLatLng(results[0]))
  };

  render() {
    return (
      <div id="welcome-page-container">
        <div id="banner">
          <img id="banner-image" src={banner} alt="Banner"/>
          <div id="banner-text-container">
            <h1 className="banner-text">Shopsmart</h1>
          </div>
          <div id="slogan-text">
            <h2 class="slogan-text">Get prices you deserve.</h2>
          </div>
          <div id="enter-location-container">
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
                  })}
                  />
                  {suggestions.map(suggestion => (
                  <div {...getSuggestionItemProps(suggestion)}>
                    <Button>{suggestion.description}</Button>
                    {suggestion === suggestions[suggestions.length - 1] ? (<div><Button>Use current location</Button></div>) : null}
                  </div>
                  ))}
                </div>
              )}
            </PlacesAutocomplete>
          </div>
        </div>
        <Grid container justify="center" id="features-grid-container">
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="stores"/>
              <h3 className="card-title">Create an account</h3>
              <Typography variant="body1">We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. </Typography> 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="stores"/>
              <h3 className="card-title">Add items to your list</h3>
              <Typography variant="body1">We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. </Typography> 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="stores"/>
              <h3 className="card-title">Find stores near you</h3>
              <Typography variant="body1">We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. </Typography> 
            </div>
          </Grid>
        </Grid> 
        <Grid container id="about-grid">
          <Grid item component={Card} xs>
            <div id="about-text">
              <h3 className="card-title">About Shopsmart</h3>
              <Typography variant="body1">We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. </Typography> 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="stores"/> 
            </div>
          </Grid>
        </Grid>
        <Grid container justify="center" id="features-grid-container">
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="anudeep-profile"/>
              <h3 className="card-title">Anudeep Yakkala</h3>
              We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="brett-profile"/>
              <h3 className="card-title">Brett Allen</h3>
              We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. 
            </div>
          </Grid>
          <Grid item component={Card} xs>
            <div class="feature-card">
              <img src={placeholder} alt="carolyn-profile"/>
              <h3 className="card-title">Carolyn Wang</h3>
              We aim to use modern API technology and computing to streamline the process of buying a set of items for the lowest possible cost. 
            </div>
          </Grid>
        </Grid> 
        <Footer bottom='© 2020 Shopsmart' backgroundColor='#05386b'></Footer> 
      </div>
    );
  }
}

export default GoogleApiWrapper({
    apiKey: (APIKey.APIKey())
  })(WelcomePage)