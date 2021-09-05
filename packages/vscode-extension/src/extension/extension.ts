import { BackendManagerService, BackendProxy, CapabilityResponseStatus } from "@kie-tooling-core/backend/dist/api";
import { DefaultHttpBridge } from "@kie-tooling-core/backend/dist/http-bridge";
import { QuarkusLocalServer } from "@kie-tooling-core/backend/dist/node";
import * as path from "path";
import * as vscode from "vscode";
import { MODEL_SERVICE_ID } from "../service/ids";
import { ImageService } from "../service/image/ImageService";
import { ModelCapability } from "../service/ModelCapability";
import { ModelService } from "../service/ModelService";
import { TextService } from "../service/text/TextService";
import {
  runAutoCropCommand,
  runClassifyCommand,
  runDetectCommand,
  runSentimentAnalysisCommand,
  setServerUp,
} from "./commands";

const IMAGE_CLASSIFY_COMMAND = "extension.command.image.classify";
const IMAGE_DETECT_COMMAND = "extension.command.image.detect";
const AUTO_CROP_COMMAND = "extension.command.image.autocrop";
const TEXT_SENTIMENT_COMMAND = "extension.command.text.sentiment";

let backendProxy: BackendProxy;

export async function activate(context: vscode.ExtensionContext): Promise<void> {
  const localServer = new QuarkusLocalServer(context.asAbsolutePath(path.join("dist", "server", "app.jar")));
  const backendManager = new BackendManagerService({
    bridge: new DefaultHttpBridge(),
    localHttpServer: localServer,
    lazyServices: [new ModelService(), new ImageService(), new TextService()],
  });

  await backendManager.start();

  backendProxy = new BackendProxy();
  backendProxy.registerBackendManager(backendManager);

  context.subscriptions.push(
    vscode.commands.registerCommand(IMAGE_CLASSIFY_COMMAND, (uri: vscode.Uri) => runClassifyCommand(uri, backendProxy)),
    vscode.commands.registerCommand(IMAGE_DETECT_COMMAND, (uri: vscode.Uri) => runDetectCommand(uri, backendProxy)),
    vscode.commands.registerCommand(AUTO_CROP_COMMAND, (uri: vscode.Uri) => runAutoCropCommand(uri, backendProxy)),
    vscode.commands.registerCommand(TEXT_SENTIMENT_COMMAND, () =>
      runSentimentAnalysisCommand(vscode.window.activeTextEditor?.document.getText(), backendProxy)
    )
  );

  await finishLoadServices(backendManager, localServer);
}

export function deactivate(): void {
  backendProxy?.stopServices();
}

async function finishLoadServices(backendManager: BackendManagerService, localServer: QuarkusLocalServer) {
  const isServerUp = (await backendManager.getService(localServer.identify())) !== undefined;

  if (!isServerUp) {
    vscode.window.showWarningMessage("Something went wrong. The local Quarkus server cannot be started up.");
    return;
  }

  await loadModels();
  await setServerUp();
}

async function loadModels() {
  try {
    const response = await backendProxy.withCapability(MODEL_SERVICE_ID, async (capability: ModelCapability) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Notification,
          title: "Checking the models. It could take a while in the first usage.",
        },
        () => capability.loadModels()
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
    }
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}
