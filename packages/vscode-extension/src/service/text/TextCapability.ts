import { Capability, CapabilityResponse } from "@kie-tooling-core/backend/dist/api";
import { TextDescriptor } from "../../model/text/TextDescriptor";

export interface TextCapability extends Capability {
  /**
   * Analyse the sentiment of the given text and report back the descriptor.
   * @param text Text to be analyzed.
   */
  analyzeSentiment(text: string): Promise<CapabilityResponse<TextDescriptor>>;
}
