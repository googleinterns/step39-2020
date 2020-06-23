import React, { Component } from 'react';
import { Checkbox, FormGroup, FormControlLabel, List, ListItem, ListItemText, Button } from '@material-ui/core';
import { Alert } from '@material-ui/lab';

const items = [
  'Milk',
  'Bread',
  'Butter',
  'Orange Juice',
  'Burger Buns',
  'Taco Shells',
  'Pinto Beans',
];

/*
 * This class displays a checkbox list containing the items returned from the Items API. 
 * The selected items are displayed below when the form is submitted. 
 */
class ListPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedItemsList: null,
      errorMessage: null,
    };
  }

  componentWillMount = () => {
    this.selectedItems = new Set();
  }

  handleChange = (event) => {
    if (event.target.checked) {
      this.selectedItems.add(event.target.name);
    } else {
      this.selectedItems.delete(event.target.name);
    }
  }

  onSubmit = () => {
    const arr = [...this.selectedItems];
    if (arr.length === 0) {
      this.setState({
        selectedItemsList: null,
        errorMessage: (<Alert severity="error">Please select at least one item!</Alert>),
      });
      return;
    }
    const listItems = arr.map((item) => (
      <ListItem key={item}>
        <ListItemText
          primary={item}
          data-testid='list item'
          />
      </ListItem>
    ));
    this.setState({
      selectedItemsList: listItems,
      errorMessage: null,
    });
  }

  render() {
    const checkboxItems = items.map((item) => (
      <FormControlLabel
        control={<Checkbox name={item} data-testid='checkbox item'/>}
        label={item}
        key={item}
        onChange={this.handleChange}
        />
    ));

    return (
      <div>
        {this.state.errorMessage}
        <FormGroup>
          {checkboxItems}
        </FormGroup>
        <Button onClick={this.onSubmit}>Submit</Button>
        <List>
          {this.state.selectedItemsList}
        </List>
      </div>
    );
  }
}

export { ListPage };