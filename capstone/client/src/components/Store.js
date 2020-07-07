import { createConnectedStore } from 'undux';

let initialState = {
  loggedIn: false,
  userId: -1,
};

export const Store = createConnectedStore(initialState);