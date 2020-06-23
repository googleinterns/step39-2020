import React from 'react';
import { render, fireEvent, screen } from '@testing-library/react';
import { ListPage } from './ListPage.js';

test('select-all', () => {
  render(<ListPage />);
  expect(screen.queryAllByTestId('list item')).toHaveLength(0);
  const checkboxElements = screen.getAllByTestId('checkbox item');
  let numItems = 0;
  checkboxElements.forEach((element) => {
    const checkbox = element.querySelector('input[type="checkbox"]');
    expect(checkbox.checked).toEqual(false);
    fireEvent.click(checkbox)
    expect(checkbox.checked).toEqual(true);
    numItems++;
  });
  fireEvent.click(screen.getByText(/Submit/i));
  const listElements = screen.getAllByTestId('list item');
  expect(listElements).toHaveLength(numItems);
});

test('select-none', async () => {
  render(<ListPage />);
  expect(screen.queryAllByTestId('list item')).toHaveLength(0);
  fireEvent.click(screen.getByText(/Submit/i));
  const alert = await screen.findByRole('alert')
  expect(alert).toHaveTextContent(/Please select at least one item!/i)
  const listElements = screen.queryAllByTestId('list item');
  expect(listElements).toHaveLength(0);
});

test('select-milk-and-butter', () => {
    render(<ListPage />);
    expect(screen.queryAllByTestId('list item')).toHaveLength(0);
    const milkCheckbox = screen.getByLabelText(/Milk/i)
    expect(milkCheckbox.checked).toEqual(false);
    fireEvent.click(milkCheckbox);
    expect(milkCheckbox.checked).toEqual(true);
    const butterCheckbox = screen.getByLabelText(/Butter/i)
    expect(butterCheckbox.checked).toEqual(false);
    fireEvent.click(butterCheckbox);
    expect(butterCheckbox.checked).toEqual(true);
    fireEvent.click(screen.getByText(/Submit/i));

    const listElements = screen.queryAllByTestId('list item');
    expect(listElements).toHaveLength(2);
    expect(listElements[0]).toHaveTextContent(/Milk/i);
    expect(listElements[1]).toHaveTextContent(/Butter/i);
})