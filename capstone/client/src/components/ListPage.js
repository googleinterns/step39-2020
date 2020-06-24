import React, { Component } from 'react';
import { Checkbox, FormGroup, FormControlLabel, List, ListItem, ListItemText, Button, Grid, Card, Radio, RadioGroup } from '@material-ui/core';
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
      alert: null,
      distanceValue: 4,
    };
  }

  componentWillMount = () => {
    this.selectedItems = new Set();
  }

  handleItemChange = (event) => {
    if (event.target.checked) {
      this.selectedItems.add(event.target.name);
    } else {
      this.selectedItems.delete(event.target.name);
    }
  }

  handleDistanceChange = (event) => {
    this.setState({
      distanceValue: parseInt(event.target.value),
    })
  }

  onSubmit = () => {
    const arr = [...this.selectedItems];
    if (arr.length === 0) {
      this.setState({
        selectedItemsList: null,
        alert: (<Alert severity="error">Please select at least one item!</Alert>),
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
      alert: null,
    });
  }

  onSave = () => {
    const arr = [...this.selectedItems];
    if (arr.length === 0) {
      this.setState({
        alert: (<Alert severity="error">Please select at least one item!</Alert>),
      })
    } else {
      this.setState({
        alert: (<Alert severity="success">Your list has been saved!</Alert>),
      });
    }
  }

  render() {
    const checkboxItems = items.map((item) => (
      <FormControlLabel
        control={<Checkbox name={item} data-testid='checkbox item'/>}
        label={item}
        key={item}
        onChange={this.handleItemChange}
        />
    ));

    const distances = [2, 4, 6, 8, 10, 12, 14].map((item) => (
      <FormControlLabel
        control={<Radio name={item + " mile radius"}/>}
        label={item + " mile radius"}
        value={item}
        key={item}
        onChange={this.handleDistanceChange}
        />
    ));

    return (
      <div id="list-page-container">
        {this.state.alert}
        <h1>Preferences</h1>
        <Grid container alignItems="stretch">
          <Grid id="distance-list-container" item component={Card} xs>
            <p>I would like to choose from stores in a</p>
            <RadioGroup id="distance-list" value={this.state.distanceValue}>
              {distances} 
            </RadioGroup>
          </Grid>
          <Grid id="items-list-container" item component={Card} xs>
            <p>Select items to add to your list</p>
            <FormGroup id="items-list">
              {checkboxItems}
            </FormGroup>
            <Button  onClick={this.onSave}  variant="contained">Save List</Button>
            <List>
              {this.state.selectedItemsList}
            </List>
          </Grid>
        </Grid>
        <Button  onClick={this.onSubmit} color="primary" variant="contained">Find Stores</Button>
      </div>
    );
  }
}

export { ListPage };