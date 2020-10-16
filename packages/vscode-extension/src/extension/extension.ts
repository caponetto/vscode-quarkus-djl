import { BackendManagerService, BackendProxy, CapabilityResponseStatus } from "@kogito-tooling/backend/dist/api";
import { DefaultHttpBridge } from "@kogito-tooling/backend/dist/http-bridge";
import { QuarkusLocalServer } from "@kogito-tooling/backend/dist/node";
import * as path from "path";
import * as vscode from "vscode";
import { ImageClassifierService, SERVICE_ID } from "../service/ImageClassifierService";

let backendProxy: BackendProxy;

export async function activate(context: vscode.ExtensionContext): Promise<void> {
  const localServer = new QuarkusLocalServer(context.asAbsolutePath(path.join("dist", "server", "app.jar")));
  const backendManager = new BackendManagerService({
    bridge: new DefaultHttpBridge(),
    localHttpServer: localServer,
    lazyServices: [new ImageClassifierService()]
  });

  await backendManager.start();

  backendProxy = new BackendProxy();
  backendProxy.registerBackendManager(backendManager);

  context.subscriptions.push(
    vscode.commands.registerCommand("extension.command.classify", (uri: vscode.Uri) => runClassifyCommand(uri))
  );

  const isServerUp = (await backendManager.getService(localServer.identify())) !== undefined;
  vscode.commands.executeCommand("setContext", "serverUp", isServerUp);
}

export function deactivate(): void {
  backendProxy?.stopServices();
}

async function runClassifyCommand(uri: vscode.Uri) {
  try {
    const response = await backendProxy.withCapability(SERVICE_ID, async (capability: ImageClassifierService) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Window,
          title: "Please wait while the classification is perfomed."
        },
        () => capability.classify(uri.fsPath)
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    vscode.window.showInformationMessage(`Best match: ${response.body!.className} (${response.body!.probability}%)`);
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}
