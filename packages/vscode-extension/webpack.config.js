const CopyWebpackPlugin = require("copy-webpack-plugin");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");

module.exports = [
  merge(common, {
    output: {
      library: "ImageServices",
      libraryTarget: "umd",
      umdNamedDefine: true
    },
    externals: {
      vscode: "commonjs vscode"
    },
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts"
    },
    plugins: [new CopyWebpackPlugin([{ from: "../app-server/target/quarkus-app-*-runner.jar", to: "server/app.jar" }])]
  })
];
