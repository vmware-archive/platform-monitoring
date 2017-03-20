import React from 'react'
import {DefaultButton} from 'pui-react-buttons'
import {HighlightPanel} from 'pui-react-panels'
import {Icon} from 'pui-react-iconography'

import Box from './Box'
import LineGraph from './LineGraph'

export default class OpsPanel extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      selectedMetricName: props.summation[0][0].name,
    }
  }

  handleClickBox(metricName) {
    this.setState({selectedMetricName: metricName})
  }

  render() {
    const {title, summation, points} = this.props

    const actions = <div><DefaultButton flat><Icon src="more_vert"/></DefaultButton></div>
    const header = <h2>{title}</h2>
    const boundBoxClickHandler = this.handleClickBox.bind(this)

    const boxRows = summation.map((row, i) => {
      const boxes = row.map((boxData, i) => <Box left={boxData.name}
                                                 key={i}
                                                 right={boxData.interval}
                                                 body={boxData.state}
                                                 status={boxData.status}
                                                 onClick={() => boundBoxClickHandler(boxData.name)}/>)
      return <div key={i} className="boxes-row">{boxes}</div>
    })

    const selectedMetric = points.find(p => {
      return p.name == this.state.selectedMetricName
    })
    const body = <div id="logging-performance">
      {boxRows}
      <div className="panel-graph">
        <LineGraph title={selectedMetric.name} points={selectedMetric.points}/>
      </div>
    </div>

    const footer = <a>View logging dashboard</a>

    return <HighlightPanel shadowLevel={2} header={header} actions={actions} footer={footer}>
      {body}
    </HighlightPanel>
  }
}
