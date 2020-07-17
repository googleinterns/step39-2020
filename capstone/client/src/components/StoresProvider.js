import React, { Component } from 'react';

class StoresProvider extends Component {
  state = {storeIndex: ''}

  render() {
    return (
      <StoresContext.Provider value={
          {
            state: this.state,
            setStore: (value) => this.setState({
              storeIndex: value
            })
          }
        }>
        {this.props.children}
        </StoresContext.Provider>
    )
  }
}

export const StoresContext = React.createContext()
export { StoresProvider }