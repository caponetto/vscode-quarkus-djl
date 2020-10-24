import { BackendManagerService, BackendProxy, CapabilityResponseStatus } from "@kogito-tooling/backend/dist/api";
import { QuarkusLocalServer } from "@kogito-tooling/backend/dist/node";
import * as vscode from "vscode";
import { ImageDescriptor } from "../model/ImageDescriptor";
import { IMAGE_SERVICE_ID } from "../service/ids";
import { ImageService } from "../service/ImageService";

export async function runClassifyCommand(uri: vscode.Uri, backendProxy: BackendProxy) {
  try {
    const response = await backendProxy.withCapability(IMAGE_SERVICE_ID, async (capability: ImageService) =>
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

    const imageDescriptor = response.body as ImageDescriptor;
    vscode.window.showInformationMessage(
      `Best match: ${imageDescriptor.items[0].className} (${imageDescriptor.items[0].probability}%)`
    );
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}

export async function runDetectCommand(uri: vscode.Uri, backendProxy: BackendProxy) {
  try {
    const response = await backendProxy.withCapability(IMAGE_SERVICE_ID, async (capability: ImageService) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Window,
          title: "Please wait while the object detection is perfomed."
        },
        () => capability.detect(uri.fsPath)
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    const imageDescriptor = response.body as ImageDescriptor;
    const selection = await vscode.window.showInformationMessage(
      `${imageDescriptor.items.length} object(s) found in the image.`,
      "See details"
    );

    if (!selection) {
      return;
    }
    await vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(imageDescriptor.path), vscode.ViewColumn.Beside);
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}

export async function checkLocalQuarkusServerAvailability(
  backendManager: BackendManagerService,
  localServer: QuarkusLocalServer
) {
  const isServerUp = (await backendManager.getService(localServer.identify())) !== undefined;
  vscode.commands.executeCommand("setContext", "serverUp", isServerUp);
}
