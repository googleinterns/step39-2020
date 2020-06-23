import React, { Component } from 'react';
import { Checkbox, FormGroup, FormControlLabel, List, ListItem, ListItemText, Button } from '@material-ui/core';

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
    this.state = {selectedItemsList: null};
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
    const listItems = arr.map((item) => (
      <ListItem>
        <ListItemText
          primary={item}
          />
      </ListItem>
    ));
    this.setState({
      selectedItemsList: listItems
    });
  }

  render() {
    const checkboxItems = items.map((item) => (
      <FormControlLabel
        control={<Checkbox name={item} />}
        label={item}
        onChange={this.handleChange}
        />
    ));

    return (
      <div>
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