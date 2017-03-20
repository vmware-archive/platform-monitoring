import {connect} from 'react-redux'
import OpsPanel from '../components/OpsPanel'

const mapStateToProps = (state, ownProps) => {
  return {
    title: ownProps.title,
    summation: state[ownProps.type].summation,
    points: state[ownProps.type].points,
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    // actions go here
  }
}

const OpsPanelContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(OpsPanel)

export default OpsPanelContainer