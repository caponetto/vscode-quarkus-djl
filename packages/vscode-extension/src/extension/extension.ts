import { BackendManagerService, BackendProxy } from "@kogito-tooling/backend/dist/api";
import { DefaultHttpBridge } from "@kogito-tooling/backend/dist/http-bridge";
import { QuarkusLocalServer } from "@kogito-tooling/backend/dist/node";
import * as path from "path";
import * as vscode from "vscode";
import { ImageService } from "../service/ImageService";
import { checkLocalQuarkusServerAvailability, runClassifyCommand, runDetectCommand } from "./commands";

const CLASSIFY_COMMAND = "extension.command.classify";
const DETECT_COMMAND = "extension.command.detect";

let backendProxy: BackendProxy;

export async function activate(context: vscode.ExtensionContext): Promise<void> {
  const localServer = new QuarkusLocalServer(context.asAbsolutePath(path.join("dist", "server", "app.jar")));
  const backendManager = new BackendManagerService({
    bridge: new DefaultHttpBridge(),
    localHttpServer: localServer,
    lazyServices: [new ImageService()]
  });

  await backendManager.start();

  backendProxy = new BackendProxy();
  backendProxy.registerBackendManager(backendManager);

  context.subscriptions.push(
    vscode.commands.registerCommand(CLASSIFY_COMMAND, (uri: vscode.Uri) => runClassifyCommand(uri, backendProxy)),
    vscode.commands.registerCommand(DETECT_COMMAND, (uri: vscode.Uri) => runDetectCommand(uri, backendProxy))
  );

  checkLocalQuarkusServerAvailability(backendManager, localServer);
}

export function deactivate(): void {
  backendProxy?.stopServices();
}
