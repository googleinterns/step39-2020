import React from 'react';
import { Switch, Route, BrowserRouter as Router } from 'react-router-dom';

import { ListPage } from './ListPage.js';

function WebRouter() {
  return (
    <div>
      <Router>
        <Switch>
          <Route exact path='/' component={() => <ListPage />} />
        </Switch>
      </Router>
    </div>
  )
}

export  { WebRouter };