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
import { render, fireEvent, screen } from '@testing-library/react';
import { ListPageWithStore } from './ListPage.js';
import { ListPage } from './ListPage.js';
import { Store } from './Store';
import { jssPreset } from '@material-ui/core';
import axios from 'axios';


jest.mock('axios');
 test('get-item-types-correctly', () => {
  const items = ['milk', 'bread'];
  const response = {data: items};
  axios.get.mockResolvedValue(response);
  render(<Store.Container> <ListPageWithStore /> </Store.Container>);
  process.nextTick(() => {
    const checkboxElements = screen.getAllByTestId('checkbox item');
    expect(checkboxElements).toHaveLength(2);
    expect(screen.getByLabelText(/milk/i));
    expect(screen.getByLabelText(/bread/i));
    expect(axios.get).toHaveBeenCalledWith('/api/v1/get-item-types');
  });
});
 
test('select-all', () => {
  const items = ['milk', 'bread'];
  const response = {data: items};
  axios.get.mockResolvedValue(response);
  render(<Store.Container> <ListPageWithStore /> </Store.Container>);
  // Necessary for the mock response to return.
  process.nextTick(() => {
    expect(screen.queryAllByTestId('list item')).toHaveLength(0);
    const checkboxElements = screen.getAllByTestId('checkbox item');
    expect(checkboxElements).toHaveLength(2);
    let numItems = 0;
    checkboxElements.forEach((element) => {
      const checkbox = element.querySelector('input[type="checkbox"]');
      expect(checkbox.checked).toEqual(false);
      fireEvent.click(checkbox)
      expect(checkbox.checked).toEqual(true);
      numItems++;
    });
    fireEvent.click(screen.getByText(/Find Stores/i));
    const listElements = screen.getAllByTestId('list item');
    expect(listElements).toHaveLength(numItems);
  }); 
});

test('select-none', async () => {
  const items = ['milk', 'bread'];
  const response = {data: items};
  axios.get.mockResolvedValue(response);
  render(<Store.Container> <ListPageWithStore /> </Store.Container>);
  process.nextTick(async () => {
    expect(screen.queryAllByTestId('list item')).toHaveLength(0);
    fireEvent.click(screen.getByText(/Find Stores/i));
    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent(/Please select at least one item!/i)
    const listElements = screen.queryAllByTestId('list item');
    expect(listElements).toHaveLength(0);
  });
});

test('select-milk-and-butter', () => {
  const items = ['milk', 'bread'];
  const response = {data: items};
  axios.get.mockResolvedValue(response);
  render(<Store.Container> <ListPageWithStore /> </Store.Container>);
  process.nextTick(async () => {
    expect(screen.queryAllByTestId('list item')).toHaveLength(0);
    const milkCheckbox = screen.getByLabelText(/milk/i);
    expect(milkCheckbox.checked).toEqual(false);
    fireEvent.click(milkCheckbox);
    expect(milkCheckbox.checked).toEqual(true);
    const butterCheckbox = screen.getByLabelText(/bread/i);
    expect(butterCheckbox.checked).toEqual(false);
    fireEvent.click(butterCheckbox);
    expect(butterCheckbox.checked).toEqual(true);
    fireEvent.click(screen.getByText(/Find Stores/i));

    const listElements = screen.queryAllByTestId('list item');
    expect(listElements).toHaveLength(2);
    expect(listElements[0]).toHaveTextContent(/milk/i);
    expect(listElements[1]).toHaveTextContent(/bread/i);
  });
}); 