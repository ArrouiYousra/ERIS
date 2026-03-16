import { api } from "./client";

export interface Message {
  id: number;
  content: string;
  channelId: number;
  createdAt: string;
}

export interface UpdateMessagePayload {
  content?: string;
  updatedAt?: string;
}

export async function getMessageByChannel(channelId: number) {
  const { data } = await api.get(`/api/channels/${channelId}/messages`);
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
