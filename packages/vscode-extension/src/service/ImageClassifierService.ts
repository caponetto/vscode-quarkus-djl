import { CapabilityResponse, LocalHttpService } from "@kogito-tooling/backend/dist/api";
import { ClassificationResult } from "./ClassificationResult";
import { ClassifyImageCapability } from "./ClassifyImageCapability";

export const SERVICE_ID = "IMAGE_CLASSIFIER";

export class ImageClassifierService extends LocalHttpService implements ClassifyImageCapability {
  public identify(): string {
    return SERVICE_ID;
  }

  public async classify(path: string): Promise<CapabilityResponse<ClassificationResult>> {
    const response = await super.execute("/classify", { path: path });
    return CapabilityResponse.ok({ className: response.body.className, probability: response.body.probability });
  }
}
