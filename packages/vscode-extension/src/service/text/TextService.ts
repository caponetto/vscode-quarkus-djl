import { CapabilityResponse, LocalHttpService } from "@kie-tools-core/backend/dist/api";
import { TextDescriptor } from "../../model/text/TextDescriptor";
import { TextRequest } from "../../model/text/TextRequest";
import { SENTIMENT_ENDPOINT } from "../endpoints";
import { TEXT_SERVICE_ID } from "../ids";
import { TextCapability } from "./TextCapability";

export class TextService extends LocalHttpService implements TextCapability {
  public identify(): string {
    return TEXT_SERVICE_ID;
  }

  public async analyzeSentiment(text: string): Promise<CapabilityResponse<TextDescriptor>> {
    const response = await super.execute(SENTIMENT_ENDPOINT, { text: text } as TextRequest);
    return CapabilityResponse.ok(response.body);
  }
}
