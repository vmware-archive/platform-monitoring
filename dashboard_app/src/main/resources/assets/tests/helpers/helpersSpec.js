import {rotatingArray} from '../../src/helpers/helpers'

describe('helpers', () => {
  describe('#rotatingArray', () => {
    it('adds newPoint to the end of the array', () => {
      expect(rotatingArray([], 5, 1)).toEqual([5])
      expect(rotatingArray([1, 4, 9], 5, 4)).toEqual([1, 4, 9, 5])
      expect(rotatingArray([1], 5, 2)).toEqual([1, 5])
    })

    it('limits the size starting from the back of the array', () => {
      expect(rotatingArray([1, 4, 9], 5, 1)).toEqual([5])
      expect(rotatingArray([1, 4, 9], 5, 2)).toEqual([9, 5])
      expect(rotatingArray([1, 4, 9], 5, 3)).toEqual([4, 9, 5])
      expect(rotatingArray([1, 4, 9], 5, 4)).toEqual([1, 4, 9, 5])
    })

    it('pads with 0 from the left until size is met', () => {
      expect(rotatingArray([], 5, 3)).toEqual([0, 0, 5])
      expect(rotatingArray([1, 4, 9], 5, 6)).toEqual([0, 0, 1, 4, 9, 5])
    })
  })
})