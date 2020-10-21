import { Capability, CapabilityResponse } from "@kogito-tooling/backend/dist/api";
import { ImageDescriptor } from "../model/ImageDescriptor";

export interface ImageCapability extends Capability {
  /**
   * Classify an image in the given path and return a descriptor.
   * @param path Absolute path of the image.
   */
  classify(path: string): Promise<CapabilityResponse<ImageDescriptor>>;
}
