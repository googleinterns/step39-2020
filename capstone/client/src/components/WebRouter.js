import React from 'react';
import { Switch, Route } from 'react-router-dom';

import ListPage from './ListPage.js';

function WebRouter() {
  return (
    <div>
      <Switch>
        <Route exact path='/' component={() => <ListPage />} />
      </Switch>
    </div>
  )
}

export default WebRouter;