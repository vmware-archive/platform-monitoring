import React from 'react'

import {scaledPoints} from '../helpers/graphicHelpers'

const xAxisMargin = 10
const yAxisMargin = 10

// TODO: Make me nicer! Pretty beta-version right now
export default class LineGraph extends React.Component {
  // TODO: Placeholder. Make this better
  static xAxisTicks(points) {
    return <g className="yAxisTicks">
      <text x="50" y="96">{Math.round(points.length / 2)}</text>
      <text x="90" y="96">{Math.round(points.length)}</text>
    </g>
  }

  // TODO: Placeholder. Make this better
  static yAxisTicks(points) {
    return <g className="xAxisTicks">
      <text x="0" y="60">{Math.round(Math.max(...points) / 2)}</text>
      <text x="0" y="10">{Math.round(Math.max(...points))}</text>
    </g>
  }

  render() {
    const {title, points} = this.props

    return <svg preserveAspectRatio="none" viewBox="0 0 100 100">
      <polyline className="xAxis" points={`0,${100 - xAxisMargin} 100,${100 - xAxisMargin}`}/>
      <polyline className="yAxis" points={`${yAxisMargin},100 ${yAxisMargin},0`}/>
      <polyline className="line" points={scaledPoints(points, 100, xAxisMargin, 100, yAxisMargin)}/>
      {LineGraph.xAxisTicks(points)}
      {LineGraph.yAxisTicks(points)}
      <text className="title" x={yAxisMargin + 5} y="10">{title}</text>
    </svg>
  }
}