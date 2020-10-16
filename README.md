# Image classification on VS Code

<p align="center">
  <a href="documentation/example.gif"><img src="documentation/example.gif" width="700"></a>
</p>

Putting together [VS Code extension](https://code.visualstudio.com/api), [Quarkus](https://quarkus.io/), [Deep Java Library](https://djl.ai/), and image classification into a simple project.

Just for fun. :P

## Modules

1. `vscode-extension`: Activate the VS Code extension when an image is opened by initializing the Quarkus app (with support of the backend library from [Kogito Tooling](https://github.com/kiegroup/kogito-tooling)).

1. `app-server`: Quarkus application that exposes a REST endpoint for image classification.

**Note**: The Quarkus application is automatically started up and stopped when the VS Code extension is activated and deactivated, respectively. Also, the building process takes care of embedding the Quarkus application into the VS Code extension.

## Requirements

- [Maven](https://maven.apache.org/) 3.6.2 or later
- [Java](https://openjdk.java.net/install/) 11 or later

## Running the example

- Open this example in VS Code 1.43+
- In the terminal, execute `yarn run init && yarn run build:fast`
- `F5` to start debugging

Open an image file (`*.png`, `*.jpg`, or `*.jpeg`) and click on the `Classify` button.

VS Code will send a POST request to the embedded Quarkus app, which will classify the image and report back the result.

## Generating the vsix file

Run `yarn run init && yarn run build:prod` if you want to generate the `vsix` file.

Once the build process is done, the `vsix` file will be at `packages/vscode-extension/dist`.