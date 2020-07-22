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

import { GoogleApiWrapper, Map, Marker } from 'google-maps-react';
import React, { Component } from 'react';
import Geocode from 'react-geocode';

import APIKey from './APIKey.js';
import { StoresContext } from './StoresProvider.js';
import './styles.css';

class StoreMaps extends Component {
  constructor(props) {
    super(props);
    this.state = {
      latitude: 0,
      longitude: 0
    }
  } 

  getLatLangFromAddress = (address) => {
    Geocode.fromAddress(address).then((res) => {
      const {lat, lng} = res.results[0].geometry.location;
      this.setState({
          latitude: lat,
          longitude: lng
      });
    }).catch((error) => {
      this.setState({
        errorMessage: "There was an error showing store locations.",
      })
    });
  }

  componentDidMount = () => {
    Geocode.setApiKey(APIKey.GeocodeAPIKey());
    this.getLatLangFromAddress(this.props.store.storeAddress);
  }

  render() {
    const maps = {
      height : "25%",
      width : "25%",
    }
    return (
      <StoresContext.Consumer> 
        {(context) => (
          <Map id="google-map" google={this.props.google} style={maps} zoom={14}
            center={{lat: this.state.latitude, lng: this.state.longitude}}>
            <Marker position={{lat: this.state.latitude, lng: this.state.longitude}}/>
          </Map>
        )}
      </StoresContext.Consumer>
    )
  }
}

export default GoogleApiWrapper({
  apiKey: (APIKey.APIKey())
})(StoreMaps)