export interface ImageRequest {
  /**
   * Absolute path of the image.
   */
  path: string;

  /**
   * Consider only k classes with the highest probability, k >= 1.
   */
  topK: number;

  /**
   * Consider only classes with probability >= threshold, 0 <= threshold <= 100.
   */
  threshold: number;
}
