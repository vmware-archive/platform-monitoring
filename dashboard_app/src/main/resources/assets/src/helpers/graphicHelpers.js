// maxX and maxY refer to the viewbox of the containing element
// xAxisMargin and yAxisMargin refer to the amount of space allocated between the viewbox edge and where we're drawing
// the axes

// TODO: rename maxY/maxX
export const scaledPoints = (points, maxX, xAxisMargin, maxY, yAxisMargin) => {
  if (points.length < 2) {
    throw 'Please provide at least 2 points.'
  }

  const minPoint = Math.min(...points)
  const maxPoint = Math.max(...points)

  const xDifference = points.length - 1
  const yDifference = maxPoint - minPoint ? maxPoint - minPoint : 1

  const scaleOfX = (maxX - yAxisMargin) / xDifference ? (maxX - yAxisMargin) / xDifference : 1
  const scaleOfY = (maxY - xAxisMargin) / yDifference ? (maxY - xAxisMargin) / yDifference : 1

  return points
    .map((p, i) => {
      return `${yAxisMargin + i * scaleOfX},${maxY - xAxisMargin - ((p - minPoint) * scaleOfY)}`
    })
    .join(' ')
}