import {scaledPoints} from '../../src/helpers/graphicHelpers'

describe('graphicHelpers', () => {
  describe('#scaledPoints', () => {
    describe('translating points into coordinates with y flipped (because 0 is top, 100 is bottom)', () => {
      it('handles linear increasing scale', () => {
        expect(scaledPoints([0, 1, 2], 100, 0, 100, 0)).toEqual('0,100 50,50 100,0')
      })

      it('handles constant linear scale', () => {
        expect(scaledPoints([1, 1, 1], 100, 0, 100, 0)).toEqual('0,100 50,100 100,100')
      })

      it('handles zeros', () => {
        expect(scaledPoints([0, 0, 0], 100, 0, 100, 0)).toEqual('0,100 50,100 100,100')
      })

      it('handles linear decreasing scale', () => {
        expect(scaledPoints([2, 1, 0], 100, 0, 100, 0)).toEqual('0,0 50,50 100,100')
      })

      it('handles linear scale with no pattern', () => {
        expect(scaledPoints([0, 1, 0, 2, 1, 0], 100, 0, 100, 0)).toEqual('0,100 20,50 40,100 60,0 80,50 100,100')
      })

      it('scales points to maxX and maxY', () => {
        expect(scaledPoints([0, 1, 2], 200, 0, 200, 0)).toEqual('0,200 100,100 200,0')
      })

      it('handles large scale', () => {
        expect(scaledPoints([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 100, 0, 100, 0))
          .toEqual('0,100 10,90 20,80 30,70 40,60 50,50 60,40 70,30 80,20 90,10 100,0')
      })
    })

    it('plots first point at x=0, last point at x=maxY', () => {
      expect(scaledPoints([3, 19], 92038, 0, 100, 0)).toEqual('0,100 92038,0')
    })

    it('plots min(points) at y=maxY, max(points) at y=0', () => {
      expect(scaledPoints([3, 19], 100, 0, 90234, 0)).toEqual('0,90234 100,0')
    })

    it('scales points according to maxY', () => {
      expect(scaledPoints([1, 2], 100, 0, 30, 0)).toEqual('0,30 100,0')
      expect(scaledPoints([1, 2], 100, 0, 29, 0)).toEqual('0,29 100,0')
      expect(scaledPoints([1, 2], 100, 0, 78398, 0)).toEqual('0,78398 100,0')
    })

    it('scales points according to maxX', () => {
      expect(scaledPoints([1, 2], 30, 0, 100, 0)).toEqual('0,100 30,0')
      expect(scaledPoints([1, 2], 29, 0, 100, 0)).toEqual('0,100 29,0')
      expect(scaledPoints([1, 2], 78398, 0, 100, 0)).toEqual('0,100 78398,0')
    })

    it('moves all points UP by xMargin', () => {
      expect(scaledPoints([0, 1, 2], 100, 15, 115, 0)).toEqual('0,100 50,50 100,0')
    })

    it('moves all points RIGHT by yMargin', () => {
      expect(scaledPoints([0, 1, 2], 115, 0, 100, 15)).toEqual('15,100 65,50 115,0')
    })

    it('reduces the y-scale by xMargin', () => {
      expect(scaledPoints([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 100, 15, 115, 0))
        .toEqual('0,100 10,90 20,80 30,70 40,60 50,50 60,40 70,30 80,20 90,10 100,0')
    })

    it('reduces the x-scale by yMargin', () => {
      expect(scaledPoints([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 115, 0, 100, 15))
        .toEqual('15,100 25,90 35,80 45,70 55,60 65,50 75,40 85,30 95,20 105,10 115,0')
    })

    xdescribe('when <2 points are given', () => {
      it('errors', () => {
        expect(scaledPoints([], 100, 0, 100, 0)).toThrow(Error)
        expect(scaledPoints([1], 100, 0, 100, 0)).toThrow(Error)
      })
    })
  })
})