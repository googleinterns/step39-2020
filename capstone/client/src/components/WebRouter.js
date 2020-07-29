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
import { Switch, Route, BrowserRouter as Router } from 'react-router-dom';

import { ListPageWithStore } from './ListPage.js';
import { HeaderWithStore } from './Header.js';
import { Store } from './Store';
import { StorePageWithStore } from './StoresPage';
import { WelcomePageWithStore } from './WelcomePage.js';

function WebRouter() {
  return (
    <div>
      <Router>
        <Store.Container>
          <Switch>
            <Route exact path='/' component={() => <div><HeaderWithStore title="Welcome"/><WelcomePageWithStore/></div>} />
            <Route path='/lists/:params' component={() => <div><HeaderWithStore title="Lists"/><ListPageWithStore/></div>} />
            <Route path='/lists' component={() => <div><HeaderWithStore title="Lists"/><ListPageWithStore /></div>} />
            <Route path='/stores/:params' component={() => <div><HeaderWithStore title="Store Recommendations"/><StorePageWithStore/></div>} />
            <Route path='/stores' component={() => <div><HeaderWithStore title="Store Recommendations"/><StorePageWithStore/></div>} />
          </Switch>
        </Store.Container>
      </Router>
    </div>
  )
}

export  { WebRouter };