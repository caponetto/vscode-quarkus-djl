import { BackendManagerService, BackendProxy } from "@kogito-tooling/backend/dist/api";
import { DefaultHttpBridge } from "@kogito-tooling/backend/dist/http-bridge";
import { QuarkusLocalServer } from "@kogito-tooling/backend/dist/node";
import * as path from "path";
import * as vscode from "vscode";
import { ImageService } from "../service/image/ImageService";
import { TextService } from "../service/text/TextService";
import {
  checkLocalQuarkusServerAvailability,
  runClassifyCommand,
  runDetectCommand,
  runSentimentAnalysisCommand
} from "./commands";

const IMAGE_CLASSIFY_COMMAND = "extension.command.image.classify";
const IMAGE_DETECT_COMMAND = "extension.command.image.detect";
const TEXT_SENTIMENT_COMMAND = "extension.command.text.sentiment";

let backendProxy: BackendProxy;

export async function activate(context: vscode.ExtensionContext): Promise<void> {
  const localServer = new QuarkusLocalServer(context.asAbsolutePath(path.join("dist", "server", "app.jar")));
  const backendManager = new BackendManagerService({
    bridge: new DefaultHttpBridge(),
    localHttpServer: localServer,
    lazyServices: [new ImageService(), new TextService()]
  });

  await backendManager.start();

  backendProxy = new BackendProxy();
  backendProxy.registerBackendManager(backendManager);

  context.subscriptions.push(
    vscode.commands.registerCommand(IMAGE_CLASSIFY_COMMAND, (uri: vscode.Uri) => runClassifyCommand(uri, backendProxy)),
    vscode.commands.registerCommand(IMAGE_DETECT_COMMAND, (uri: vscode.Uri) => runDetectCommand(uri, backendProxy)),
    vscode.commands.registerCommand(TEXT_SENTIMENT_COMMAND, () =>
      runSentimentAnalysisCommand(vscode.window.activeTextEditor?.document.getText(), backendProxy)
    )
  );

  checkLocalQuarkusServerAvailability(backendManager, localServer);
}

export function deactivate(): void {
  backendProxy?.stopServices();
}
