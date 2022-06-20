import { Capability, CapabilityResponse } from "@kie-tools-core/backend/dist/api";
import { ImageDescriptor } from "../../model/image/ImageDescriptor";

export interface ImageCapability extends Capability {
  /**
   * Classify an image in the given path and return a descriptor.
   * @param path Absolute path of the image.
   * @returns A descriptor of the image.
   */
  classify(path: string): Promise<CapabilityResponse<ImageDescriptor>>;

  /**
   * Detect objects on an image in the given path and return a descriptor.
   * @param path Absolute path of the image.
   * @returns A descriptor of the image.
   */
  detect(path: string): Promise<CapabilityResponse<ImageDescriptor>>;

  /**
   * Detect objects on an image in the given path and crop them into new files.
   * @param path Absolute path of the image.
   * @returns The list of paths of the output files.
   */
  autoCrop(path: string): Promise<CapabilityResponse<string[]>>;

  /**
   * Generate random images related to given image.
   * @param path Absolute path of the image that should be related to the output images.
   * @returns The list of paths of the output files.
   */
  generateRandomImages(path: string): Promise<CapabilityResponse<string[]>>;
}
