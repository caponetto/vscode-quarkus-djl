export interface TextDescriptor {
  /**
   * Whether the sentiment extracted from the text is positive or not.
   */
  positive: boolean;

  /**
   * Probability associated with the extracted sentiment.
   */
  sentimentProbability: number;
}
