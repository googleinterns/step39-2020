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
import { Chip } from '@material-ui/core';

class FilterStores extends Component {
  constructor(props) {
    super(props)
    this.state = {
      originalStores: this.props.originalStores,
      items: this.props.items,
      selectedFilters: new Set(),
    }
    this.onFilterAdd = this.onFilterAdd.bind(this);
    this.onFilterRemove = this.onFilterRemove.bind(this);
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
    const stores = this.state.originalStores.filter(store => {
      const storeItems = new Set();
      store.items.forEach((item) => storeItems.add(item.type));
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
    const filter = this.state.items.map(item => (this.state.selectedFilters.has(item) ?
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
    ));

    return(
      <div>{filter}</div>
    );
  }
}

export default FilterStores;