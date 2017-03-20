const getValue = (json, identifier) => {
  return json.find(o => {
    return o.name == identifier
  }).value
}

export const updateMetrics = dispatch => {
  dispatch({type: 'METRICS_UPDATE_START'})

  fetch('/loggregator')
    .then(response => response.json())
    .then(json => {
      if (json.length == 0) {
        return
      }

      const lossRate = getValue(json, 'calculatedMetric.Firehose.LossRate')
      const throughput = getValue(json, 'DopplerServer.listeners.receivedEnvelopes')

      dispatch({
        type: 'METRICS_UPDATE_SUCCESS',
        metrics: {lossRate: lossRate, throughput: throughput}
      })
    })
    .catch(e => dispatch({type: 'METRICS_UPDATE_FAILURE', error: e}))
}