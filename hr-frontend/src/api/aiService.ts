import { aiApi } from './client';

export interface PolishTextRequest {
  text: string;
  context?: string;
}

export interface PolishTextResponse {
  originalText: string;
  polishedText: string;
  model: string;
}

export const aiService = {
  polishText: async (text: string, context?: string): Promise<PolishTextResponse> => {
    const response = await aiApi.post<PolishTextResponse>('/polish', {
      text,
      context,
    });
    return response.data;
  },
};
