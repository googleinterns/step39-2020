import React from 'react';
import { Switch, Route, BrowserRouter as Router } from 'react-router-dom';

import { ListPageWithStore } from './ListPage.js';
import { HeaderWithStore } from './Header.js';
import { Store } from './Store';

function WebRouter() {
  return (
    <div>
      <Router>
        <Store.Container>
          <HeaderWithStore />
          <Switch>
            <Route exact path='/' component={() => <ListPageWithStore />} />
          </Switch>
        </Store.Container>
      </Router>
    </div>
  )
}

export  { WebRouter };