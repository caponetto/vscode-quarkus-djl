import { ImageItem } from "./ImageItem";

export interface ImageDescriptor {
  /**
   * Absolute path of the image.
   */
  path: string;

  /**
   * List of items, which describes each element found in the image.
   */
  items: ImageItem[];
}
