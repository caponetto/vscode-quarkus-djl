import { CapabilityResponse, LocalHttpService } from "@kie-tools-core/backend/dist/api";
import { ImageDescriptor } from "../../model/image/ImageDescriptor";
import { ImageRequest } from "../../model/image/ImageRequest";
import { AUTO_CROP_ENDPOINT, CLASSIFY_ENDPOINT, DETECT_ENDPOINT, GENERAGE_RANDOM_IMAGES_ENDPOINT } from "../endpoints";
import { IMAGE_SERVICE_ID } from "../ids";
import { ImageCapability } from "./ImageCapability";

export class ImageService extends LocalHttpService implements ImageCapability {
  public identify(): string {
    return IMAGE_SERVICE_ID;
  }

  public async classify(path: string): Promise<CapabilityResponse<ImageDescriptor>> {
    const response = await super.execute(CLASSIFY_ENDPOINT, { path: path, threshold: 30, topK: 1 } as ImageRequest);
    return CapabilityResponse.ok(response.body);
  }

  public async detect(path: string): Promise<CapabilityResponse<ImageDescriptor>> {
    const response = await super.execute(DETECT_ENDPOINT, { path: path, threshold: 30 } as ImageRequest);
    return CapabilityResponse.ok(response.body);
  }

  public async autoCrop(path: string): Promise<CapabilityResponse<string[]>> {
    const response = await super.execute(AUTO_CROP_ENDPOINT, { path: path, threshold: 30 } as ImageRequest);
    return CapabilityResponse.ok(response.body);
  }

  public async generateRandomImages(path: string): Promise<CapabilityResponse<string[]>> {
    const response = await super.execute(GENERAGE_RANDOM_IMAGES_ENDPOINT, {
      path: path,
      threshold: 30,
      topK: 3,
    } as ImageRequest);
    return CapabilityResponse.ok(response.body);
  }
}
