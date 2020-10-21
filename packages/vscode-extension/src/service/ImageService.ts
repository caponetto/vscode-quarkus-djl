import { CapabilityResponse, LocalHttpService } from "@kogito-tooling/backend/dist/api";
import { ImageDescriptor } from "../model/ImageDescriptor";
import { ImageRequest } from "../model/ImageRequest";
import { ImageCapability } from "./ImageCapability";

export const SERVICE_ID = "IMAGE";

export class ImageService extends LocalHttpService implements ImageCapability {
  public identify(): string {
    return SERVICE_ID;
  }

  public async classify(path: string): Promise<CapabilityResponse<ImageDescriptor>> {
    const response = await super.execute("/classify", { path: path, topK: 1, threshold: 0 } as ImageRequest);
    return CapabilityResponse.ok<ImageDescriptor>(response.body as ImageDescriptor);
  }
}
