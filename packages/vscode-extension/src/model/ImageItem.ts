import { BoundingBox } from "./BoundingBox";

export interface ImageItem {
  /**
   * Identified class for this image item.
   */
  className: string;

  /**
   * Probability associated with the class, 0 <= probability <= 100.
   */
  probability: number;

  /**
   * Bounding box associated with the class - only available for object detection.
   */
  boundingBox: BoundingBox | undefined;
}
