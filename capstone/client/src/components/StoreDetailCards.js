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

import { Accordion,  AccordionDetails, AccordionSummary, Avatar, Card, Grid, List, ListItem, ListItemText, Typography } from '@material-ui/core';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

import { StoresContext } from './StoresProvider.js';
import StoreMaps from './StoreMaps.js';
import './styles.css';

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

import safeway from './images/safeway-logo.png';
import walmart from './images/walmart-logo.jpg';
import target from './images/target-logo.png';

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

const logos = {
  "S" : safeway,
  "W" : walmart,
  "T" : target
}

class StoreDetailCards extends Component {
  render() {
    const storeDetailCards = this.props.stores.map((store) => (
      <div id="store-details-container">
        <img id="store-logo" src={logos[store.storeName[0]]} alt=""/>
        <Typography variant='h4'>{store.storeName}</Typography>
        <Typography variant='h6'>Address: {store.storeAddress}</Typography>
        <Grid container alignItems="stretch">
          <Grid item component={Card} xs>
            <StoreMaps store={store} userLat={this.props.userLat} userLong={this.props.userLong} />
          </Grid>
          <Grid item component={Card} xs>
            <Typography variant='subtitle1'>This store has the following items:</Typography>
              {Object.keys(store.items).map((itemType, i) => (
                <Accordion>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />} aria-label="Expand">
                    <Typography variant='subtitle1' style={{display: 'flex', alignItems:'center'}}><Avatar id="item-icon" src={images[itemType]} alt={images[itemType]}/>&nbsp;&nbsp;{itemType}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <List>
                    {Object.keys(store.items[itemType].sort((a, b) => a.itemPrice - b.itemPrice)).map((index, i) => (
                      <ListItem key = {i}>
                        <ListItemText>
                          {store.items[itemType][index].itemName} (${(store.items[itemType][index].itemPrice-.005).toFixed(2)})
                        </ListItemText>
                      </ListItem>
                    ))}
                    </List>
                  </AccordionDetails>
                </Accordion>
              ))}
          </Grid>
        </Grid>
      </div>
    ));
      
    return (
        <StoresContext.Consumer> 
          {(context) => (
            <div>{storeDetailCards[context.state.storeIndex]}</div>
          )}
        </StoresContext.Consumer>
    )
  }
}

export default StoreDetailCards;