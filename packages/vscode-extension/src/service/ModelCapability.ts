import { Capability, CapabilityResponse } from "@kie-tooling-core/backend/dist/api";

export interface ModelCapability extends Capability {
  /**
   * Try to load all available models.
   * The models will be downloaded in case they are not available yet.
   */
  loadModels(): Promise<CapabilityResponse<void>>;
}
