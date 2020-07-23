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
import { Chip, Input, Grid, Slider } from '@material-ui/core';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';

class FilterStores extends Component {
  constructor(props) {
    super(props)
    this.state = {
      originalStores: this.props.originalStores,
      items: this.props.items,
      selectedFilters: new Set(),
      maxPrice : 0,
      setPriceLeft : 0,
      setPriceRight : 0,
      maxSet : false,
    }
    this.onFilterAdd = this.onFilterAdd.bind(this);
    this.onFilterRemove = this.onFilterRemove.bind(this);
    this.onPriceChange = this.onPriceChange.bind(this);
    this.handleInputChangeLeft = this.handleInputChangeLeft.bind(this);
    this.handleInputChangeRight = this.handleInputChangeRight.bind(this);
    this.handleBlur = this.handleBlur.bind(this);
  }

  getMaxCost() {
    var max = this.props.originalStores[0].lowestPotentialPrice;
    for(var store in this.props.originalStores) {
      if(store.lowestPotentialPrice > max) {
        max = store.lowestPotentialPrice;
      }
    }
    this.setState({
      maxPrice : max.toFixed(2),
      setPriceLeft : 0,
      setPriceRight : max.toFixed(2)
    });
  }

  onPriceChange(event, newValue) {
    this.setState({
      setPriceLeft : newValue[0],
      setPriceRight : newValue[1]
    });
    const stores = this.filterStores(this.state.selectedFilters);
    this.setState({
      stores,
    });
    this.props.onFilterChange(stores);
  }

  handleInputChangeLeft(event) {
    this.setState({
      setPriceLeft : event.target.value === '' ? 0 : Number(event.target.value)
    });
    const stores = this.filterStores(this.state.selectedFilters);
    this.setState({
      stores,
    });
    this.props.onFilterChange(stores);
  }

  handleInputChangeRight(event) {
    this.setState({
      setPriceRight : event.target.value === '' ? this.state.maxPrice : Number(event.target.value)
    });
    const stores = this.filterStores(this.state.selectedFilters);
    this.setState({
      stores,
    });
    this.props.onFilterChange(stores);
  }

  handleBlur() {
    if(this.state.setPriceLeft < 0) {
      this.setState({
        setPriceLeft : 0,
      });
    } else if (this.state.setPriceRight > this.state.maxPrice) {
      this.setState({
        setPriceRight : this.state.maxPrice,
      });
    }
  }

  onFilterAdd(event) {
    event.preventDefault();
    const selectedFilters = this.state.selectedFilters;
    selectedFilters.add(event.target.innerText);
    const stores = this.filterStores(selectedFilters);
    this.setState({
      selectedFilters,
      stores,
    });
    this.props.onFilterChange(stores);
  }

  onFilterRemove(event) {
    event.preventDefault();
    const selectedFilters = this.state.selectedFilters;
    selectedFilters.delete(event.target.innerText);
    const stores = this.filterStores(selectedFilters);
    this.setState({
      selectedFilters,
    });
    this.props.onFilterChange(stores);
  }

  filterStores(selectedFilters) {
    const stores = this.props.originalStores.filter((store) => {
      if(store.lowestPotentialPrice > this.state.setPriceRight || store.lowestPotentialPrice < this.state.setPriceLeft){
        return false;
      }
      const storeItems = new Set();
      Object.keys(store.items).forEach((item) => storeItems.add(item));
      let containsAll = true;
      selectedFilters.forEach((item) => {
        if (!storeItems.has(item)) {
          containsAll = false
        }
      });
      return containsAll;
    });
    return stores;
  }

  render() {
    const filter = (this.state.items.length > 1) ? this.state.items.map(item => (this.state.selectedFilters.has(item) ?
      <Chip 
        key={item}
        label={item}
        clickable
        onClick={this.onFilterRemove}
        color="secondary"
      /> :
      <Chip
        key={item}
        label={item}
        clickable
        onClick={this.onFilterAdd}
      />
    )) : null;

    if(!this.state.maxSet && this.props.originalStores.length > 0){
      this.getMaxCost();
      this.setState({
        maxSet : true,
      });
    }

    return(
      <Grid>
      <div id="price-filter">
        <Grid container spacing={2} alignItems="center">
          <Grid item>
            <AttachMoneyIcon color='primary' />
          </Grid>
          <Grid item>
            <Input
              value={this.state.setPriceLeft}
              margin="dense"
              onChange={this.handleInputChangeLeft}
              onBlur={this.handleBlur}
              inputProps={{
                step: (this.state.maxPrice/40).toFixed(2),
                min: 0,
                max: this.state.maxPrice,
                type: 'number',
                'aria-labelledby': 'input-slider',
              }}
            />
          </Grid>
          <Grid item xs>
            <Slider
                  value={[this.state.setPriceLeft, this.state.setPriceRight]}
                  onChange={this.onPriceChange}
                  aria-labelledby="range-slider"
                  color="action"
                  valueLabelDisplay="on"
                  max={this.state.maxPrice}
                  step={(this.state.maxPrice/40).toFixed(2)}
            />
          </Grid>
          <Grid item>
            <Input
              value={this.state.setPriceRight}
              margin="dense"
              onChange={this.handleInputChangeRight}
              onBlur={this.handleBlur}
              inputProps={{
                step: (this.state.maxPrice/40).toFixed(2),
                min: 0,
                max: this.state.maxPrice,
                type: 'number',
                'aria-labelledby': 'input-slider',
              }}
            />
        </Grid>
        </Grid>
      </div>
      <div>{filter}</div>
      </Grid>
    );
  }
}

export default FilterStores;