import { api } from "./client";

export interface Message {
  id: number;
  content: string;
  channelId: number;
  createdAt: string;
}

export async function getMessageByChannel(channelId: number) {
  const { data } = await api.get(`/api/channels/${channelId}/messages`);
  return data;
}

export async function deleteMessage(messageId: number) {
  const { data } = await api.delete(`/messages/${messageId}/`);
  return data;
}
