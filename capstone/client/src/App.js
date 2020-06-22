import React, { Component } from 'react';

import { WebRouter } from './components/WebRouter.js';
import './components/styles.css';


class App extends Component {
  componentDidMount() {
    console.log("Inside componentDidMount!");
    fetch("/api/v1/test-servlet")
      .then((response) => response.text())
      .then((text) => {
        console.log("here is the text from servlet: ", text);
      });
  }

  render() {
    return (
      <WebRouter />
    );
  }
}

export { App };