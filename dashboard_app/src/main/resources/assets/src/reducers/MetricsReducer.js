import {rotatingArray} from '../helpers/helpers'

const defaultState = {
  loggingPerformance: {
    summation: [
      [
        {name: 'Loss rate', interval: '5m', state: 'Loading...'}
      ],
      [
        {name: 'Dropped msgs', interval: '5m', state: 'Loading...'},
        {name: 'Throughput', interval: '5m', state: 'Loading...'},
      ]
    ],
    points: [
      {name: 'Loss rate', points: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]},
      {name: 'Dropped msgs', points: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]},
      {name: 'Throughput', points: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]},
    ]
  },
  schoolPerformance: {
    summation: [
      [
        {name: 'Pass', interval: '5m', state: '72%', status: 'good'},
        {name: 'Fail', interval: '5m', state: '11%', status: 'good'},
        {name: 'Dropout', interval: '5m', state: '2%', status: 'good'},
      ],
      [
        {name: 'Enrolled', interval: '5m', state: '19,347'},
      ],
      [
        {name: 'Classes', interval: '5m', state: '3', status: 'bad'},
        {name: 'Clubs', interval: '5m', state: '14'},
      ],
    ],
    points: [
      {name: 'Pass', points: [72, 93, 47, 55, 88, 56]},
      {name: 'Fail', points: [17, 19, 55]},
      {name: 'Dropout', points: [20, 10, 20, 10, 5]},
      {name: 'Enrolled', points: [1, 2, 3, 4, 5, 3, 4, 1]},
      {name: 'Classes', points: [2, 4, 3]},
      {name: 'Clubs', points: [50, 19, 2, 55, 2]},
    ]
  }
}

const lossRateStatus = lossRate => {
  if (lossRate < 0.05) {
    return 'good'
  }

  if (lossRate < 0.1) {
    return 'medium'
  }

  return 'bad'
}

const lossRateSummation = lossRate => {
  return {
    name: 'Loss rate',
    interval: '5m',
    state: `${Math.round(lossRate * 100)}%`,
    status: lossRateStatus(lossRate)
  }
}

const throughputSummation = throughput => {
  return {name: 'Throughput', interval: '5m', state: throughput, status: 'good'}
}

const droppedMessagesSummation = dropped => {
  return {name: 'Dropped msgs', interval: '5m', state: dropped, status: 'bad'}
}

const findPoints = (state, name) => {
  return state.loggingPerformance.points.find(p => {
    return p.name == name
  }).points
}

export default (currentState = defaultState, action) => {
  const newState = Object.assign({}, currentState)

  try {
    switch (action.type) {
      case 'METRICS_UPDATE_START':
        console.log('Updating metrics!')
        return currentState // no-op, maybe display loading icon later
      case 'METRICS_UPDATE_SUCCESS':
        const {lossRate, throughput, dropped} = action.metrics

        const currentLossRatePoints = findPoints(currentState, 'Loss rate')
        const currentDroppedMessagesPoints = findPoints(currentState, 'Dropped msgs')
        const currentThroughputPoints = findPoints(currentState, 'Throughput')

        newState.loggingPerformance = {
          summation: [
            [
              lossRateSummation(lossRate)
            ],
            [
              throughputSummation(throughput),
              droppedMessagesSummation(dropped),
            ]
          ],
          points: [
            {name: 'Loss rate', points: rotatingArray(currentLossRatePoints, lossRate, 10)},
            {name: 'Dropped msgs', points: rotatingArray(currentDroppedMessagesPoints, dropped, 10)},
            {name: 'Throughput', points: rotatingArray(currentThroughputPoints, throughput, 10)},
          ]
        }

        return newState
      case 'METRICS_UPDATE_FAILURE':
        console.error(action.error)
        return currentState
      default:
        return currentState
    }
  } catch (e) {
    console.error(e)
  }
}