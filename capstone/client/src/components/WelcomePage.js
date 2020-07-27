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
import { Button } from '@material-ui/core';
import APIKey from './APIKey.js';
import PlacesAutocomplete, {
  geocodeByAddress,
  getLatLng,
} from 'react-places-autocomplete';

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
    );
  }
}

export default GoogleApiWrapper({
    apiKey: (APIKey.APIKey())
  })(WelcomePage)