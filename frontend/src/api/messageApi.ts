import { api } from "./client";
import type { ChatMessage } from "../types/shared";

export type Message = ChatMessage;

export interface UpdateMessagePayload {
  content?: string;
  updatedAt?: string;
}

export async function getMessageByChannel(
  channelId: number,
): Promise<ChatMessage[]> {
  const { data } = await api.get<ChatMessage[]>(
    `/api/channels/${channelId}/messages`,
  );
  return data;
}

export async function deleteMessage(messageId: number) {
  const { data } = await api.delete(`api/messages/${messageId}`);
  return data;
}

export async function editMessage(
  messageId: number,
  messageData: UpdateMessagePayload,
) {
  const { data } = await api.patch(`api/messages/${messageId}`, messageData);
  return data;
}
