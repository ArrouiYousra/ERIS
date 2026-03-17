import { api } from "./client";
import type { ChatMessage } from "../types/shared";

export type Message = ChatMessage;

export async function getMessageByChannel(
  channelId: number,
): Promise<ChatMessage[]> {
  const { data } = await api.get<ChatMessage[]>(
    `/api/channels/${channelId}/messages`,
  );
  return data;
}
