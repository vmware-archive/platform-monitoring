export const rotatingArray = (oldArray, newPoint, size) => {
  const newArr = [...oldArray, newPoint]

  while (newArr.length < size) {
    newArr.unshift(0)
  }

  if (newArr.length > size) {
    return newArr.slice(newArr.length - size, newArr.length)
  } else {
    return newArr.slice(newArr.length - size, size)
  }
}