import { CapabilityResponse, LocalHttpService } from "@kogito-tooling/backend/dist/api";
import { ImageDescriptor } from "../../model/image/ImageDescriptor";
import { ImageRequest } from "../../model/image/ImageRequest";
import { CLASSIFY_ENDPOINT, DETECT_ENDPOINT } from "../endpoints";
import { IMAGE_SERVICE_ID } from "../ids";
import { ImageCapability } from "./ImageCapability";

export class ImageService extends LocalHttpService implements ImageCapability {
  public identify(): string {
    return IMAGE_SERVICE_ID;
  }

  public async classify(path: string): Promise<CapabilityResponse<ImageDescriptor>> {
    const response = await super.execute(CLASSIFY_ENDPOINT, { path: path, topK: 1 } as ImageRequest);
    return CapabilityResponse.ok<ImageDescriptor>(response.body as ImageDescriptor);
  }

  public async detect(path: string): Promise<CapabilityResponse<ImageDescriptor>> {
    const response = await super.execute(DETECT_ENDPOINT, { path: path, threshold: 30 } as ImageRequest);
    return CapabilityResponse.ok<ImageDescriptor>(response.body as ImageDescriptor);
  }
}
