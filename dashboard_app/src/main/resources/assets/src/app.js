import 'pui-css-all'
import '../stylesheets/app.scss'

import React from 'react'
import ReactDOM from 'react-dom'
import { Provider, connect } from 'react-redux'
import { createStore } from 'redux'

import metrics from './reducers/metricsReducer'
import Page from './components/Page'
import {updateMetrics} from './actions/actions'

let store = createStore(metrics)

const updateMetricsRepeatedly = () => {
  updateMetrics(store.dispatch)
  setTimeout(updateMetricsRepeatedly, 1000)
}
updateMetricsRepeatedly()

ReactDOM.render(<Provider store={store}>
    <Page />
  </Provider>,
  document.getElementById('root'))