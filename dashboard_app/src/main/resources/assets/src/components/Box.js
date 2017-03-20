import React from 'react'
import classnames from 'classnames'

export default class Box extends React.PureComponent {
  static boxStatusClassName(status) {
    switch (status) {
      case 'good':
        return 'box--good';
      case 'bad':
        return 'box--bad';
      default:
        return 'box--neutral';
    }
  }

  render() {
    const {left, right, body, status, onClick} = this.props
    const boxClassname = classnames('box', Box.boxStatusClassName(status))

    return <div className={boxClassname} onClick={onClick}>
      <div className="box-header">
        <div className="left">{left}</div>
        <div className="right">{right}</div>
      </div>
      <div className="box-body">{body}</div>
    </div>
  }
}