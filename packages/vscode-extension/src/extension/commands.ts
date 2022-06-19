import { BackendProxy, CapabilityResponseStatus } from "@kie-tools-core/backend/dist/api";
import * as vscode from "vscode";
import { ImageDescriptor } from "../model/image/ImageDescriptor";
import { TextDescriptor } from "../model/text/TextDescriptor";
import { IMAGE_SERVICE_ID, TEXT_SERVICE_ID } from "../service/ids";
import { ImageCapability } from "../service/image/ImageCapability";
import { TextCapability } from "../service/text/TextCapability";

export async function runClassifyCommand(uri: vscode.Uri, backendProxy: BackendProxy): Promise<void> {
  try {
    const response = await backendProxy.withCapability(IMAGE_SERVICE_ID, async (capability: ImageCapability) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Notification,
          title: "Classifying ...",
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

export async function runDetectCommand(uri: vscode.Uri, backendProxy: BackendProxy): Promise<void> {
  try {
    const response = await backendProxy.withCapability(IMAGE_SERVICE_ID, async (capability: ImageCapability) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Notification,
          title: "Detecting objects ...",
        },
        () => capability.detect(uri.fsPath)
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    const imageDescriptor = response.body as ImageDescriptor;

    if (imageDescriptor.items.length === 0) {
      vscode.window.showWarningMessage("No objects have been detected :(");
      return;
    }

    const selection = await vscode.window.showInformationMessage(
      `${imageDescriptor.items.length} object(s) found in the image.`,
      "See details"
    );

    if (!selection) {
      return;
    }

    await vscode.commands.executeCommand(
      "vscode.open",
      vscode.Uri.parse(imageDescriptor.path),
      vscode.ViewColumn.Beside
    );
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}

export async function runAutoCropCommand(uri: vscode.Uri, backendProxy: BackendProxy): Promise<void> {
  try {
    const response = await backendProxy.withCapability(IMAGE_SERVICE_ID, async (capability: ImageCapability) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Notification,
          title: "Auto cropping ...",
        },
        () => capability.autoCrop(uri.fsPath)
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    const croppedPaths = response.body as string[];

    if (croppedPaths.length === 0) {
      vscode.window.showWarningMessage("No objects have been detected to be cropped :(");
      return;
    }

    const selection = await vscode.window.showInformationMessage(
      `${croppedPaths.length} cropped image(s) have been generated.`,
      "See details"
    );

    if (!selection) {
      return;
    }

    for (const path of croppedPaths) {
      await vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(path), vscode.ViewColumn.Beside);
    }
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}

export async function runSentimentAnalysisCommand(text: string | undefined, backendProxy: BackendProxy): Promise<void> {
  if (!text) {
    return;
  }

  try {
    const response = await backendProxy.withCapability(TEXT_SERVICE_ID, async (capability: TextCapability) =>
      vscode.window.withProgress(
        {
          location: vscode.ProgressLocation.Notification,
          title: "Analyzing ...",
        },
        () => capability.analyzeSentiment(text)
      )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    const textDescriptor = response.body as TextDescriptor;
    vscode.window.showInformationMessage(`This text looks ${textDescriptor.positive ? "positive :)" : "negative :("}`);
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}

export async function setServerUp(): Promise<void> {
  await vscode.commands.executeCommand("setContext", "serverUp", true);
}
