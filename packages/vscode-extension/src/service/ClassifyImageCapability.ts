import { Capability, CapabilityResponse } from "@kogito-tooling/backend/dist/api";
import { ClassificationResult } from "./ClassificationResult";

export interface ClassifyImageCapability extends Capability {
  classify(path: string): Promise<CapabilityResponse<ClassificationResult>>;
}
