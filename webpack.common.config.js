const path = require("path");

module.exports = {
  mode: "development",
  devtool: "inline-source-map",
  output: {
    path: path.resolve("./dist"),
    filename: "[name].js"
  },
  stats: {
    excludeAssets: [name => !name.endsWith(".js")],
    excludeModules: true
  },
  performance: {
    maxAssetSize: 30000000,
    maxEntrypointSize: 30000000
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")]
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader"
      }
    ]
  }
};
