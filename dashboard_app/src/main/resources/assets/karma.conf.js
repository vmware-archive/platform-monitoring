const karmaWebpackConfig = require('./webpack.config')
const path = require('path')
const localBrowsers = ['Chrome']
const ciBrowsers = [path.join(__dirname, 'chrome_script.sh')]
const browsers = process.env.TEST_ENV === 'CI' ? ciBrowsers : localBrowsers

module.exports = config => {
  config.set({
    frameworks: ['jasmine'],
    files: [
      'tests/**/*Spec.js', // needs to be spec or else random entry point errors (???)
    ],
    browsers: browsers,
    preprocessors: {
      'tests/**/*.js': ['webpack'],
      'src/**/*.js': ['webpack']
    },
    reporters: ['spec', 'kjhtml'],
    webpack: karmaWebpackConfig,
    webpackMiddleware: {noInfo: true}
  })
}