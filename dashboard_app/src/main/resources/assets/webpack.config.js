const path = require('path')
const ExtractTextPlugin = require('extract-text-webpack-plugin')

const prod = process.argv.indexOf('-p') !== -1

module.exports = {
  entry: './src/app.js',
  output: {
    path: path.resolve(__dirname, '..', 'static'),
    filename: 'bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.(eot|ttf|woff)$/,
        loader: 'url-loader',
      },
      {
        test: /\.s?css$/,
        loader: ExtractTextPlugin.extract({
          fallbackLoader: 'style-loader',
          loader: 'css-loader!sass-loader',
        })
      },
      {
        test: /\.(jpe?g|png|gif|svg)$/i,
        loader: 'file-loader!image-webpack-loader'
      }
    ],
  },
  plugins: [
    new ExtractTextPlugin('bundle.css'),
  ],
  node: {
    fs: 'empty', // so that babel doesn't blow up with weird error messages occasionally
  },
  devtool: prod ? false : 'inline-source-map'
}