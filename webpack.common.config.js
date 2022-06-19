const path = require("path");

module.exports = {
  devtool: "inline-source-map",
  output: {
    path: path.resolve("./dist"),
    filename: "[name].js",
    chunkFilename: "[name].bundle.js",
  },
  stats: {
    excludeAssets: [(name) => !name.endsWith(".js")],
    excludeModules: true,
  },
  performance: {
    maxEntrypointSize: 1024 * 1024 * 3,
    maxAssetSize: 1024 * 1024 * 35,
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: ["node_modules"],
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        enforce: "pre",
        use: ["source-map-loader"],
      },
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
      },
    ],
  },
};
