import React from 'react'

import OpsPanelContainer from '../containers/OpsPanel'

export default class Page extends React.Component {
  render() {
    return <div className="main-page">
      <OpsPanelContainer title="Logging Performance" type="loggingPerformance"/>
      <OpsPanelContainer title="School Performance" type="schoolPerformance"/>
    </div>
  }
}