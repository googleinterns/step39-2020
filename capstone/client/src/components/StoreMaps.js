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
import { Button } from '@material-ui/core';
import CommuteIcon from '@material-ui/icons/Commute';

import APIKey from './APIKey.js';
import './styles.css';

class StoreMaps extends Component {
  constructor(props) {
    super(props);
    this.state = {
      latitude: 0,
      longitude: 0,
      userAddress: null,
      url: null,
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
    Geocode.setApiKey(APIKey.APIKey());
    this.getLatLangFromAddress(this.props.store.storeAddress);
    this.getDirectionsUrl(this.props.store.storeAddress);

    Geocode.fromLatLng(this.props.userLat, this.props.userLong).then(
      response => {
        const address = response.results[0].formatted_address;
        this.setState({
          userAddress: address,
        });
        this.getDirectionsUrl(this.props.store.storeAddress);
      }
    );
  }

  componentDidUpdate = (prevProps) => {
    if (this.props.store !== prevProps.store) {
      this.getLatLangFromAddress(this.props.store.storeAddress);
      this.getDirectionsUrl(this.props.store.storeAddress);
    }
  }

  getDirectionsUrl(address) {
    let url = 'https://www.google.com/maps/dir/?api=1';
    if (this.state.userAddress) {
      url = url + '&origin=' + encodeURIComponent(this.state.userAddress);
    } 
    url = url + '&destination=' + encodeURIComponent(address);

    this.setState({
      url,
    });
  }

  redirectToDirections() {
    window.location.href = this.state.url;
  }

  render() {
    const containerStyle = {
      position: 'relative',
      width: '500px', 
      height: '400px',
      marginBottom: '20px'
    }

    return (
      <div>
        <Map id="google-map" google={this.props.google} containerStyle={containerStyle} zoom={14}
          center={{lat: this.state.latitude, lng: this.state.longitude}}>
          <Marker position={{lat: this.state.latitude, lng: this.state.longitude}}/>
        </Map>
        <Button variant="contained" id="interior-button" onClick={() => { this.redirectToDirections() }}>
          Get Directions&nbsp;<CommuteIcon color='black' />
        </Button>
      </div>
    )
  }
}

export default GoogleApiWrapper({
  apiKey: (APIKey.APIKey())
})(StoreMaps)