import { CapabilityResponse, LocalHttpService } from "@kie-tooling-core/backend/dist/api";
import { IMAGE_MODELS_ENDPOINT, TEXT_MODELS_ENDPOINT } from "./endpoints";
import { MODEL_SERVICE_ID } from "./ids";
import { ModelCapability } from "./ModelCapability";

export class ModelService extends LocalHttpService implements ModelCapability {
  public identify(): string {
    return MODEL_SERVICE_ID;
  }

  public async loadModels(): Promise<CapabilityResponse<void>> {
    for (const endpoint of [IMAGE_MODELS_ENDPOINT, TEXT_MODELS_ENDPOINT]) {
      await super.execute(endpoint);
    }
    return CapabilityResponse.ok();
  }
}
