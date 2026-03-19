import { api } from "./client";
import type { ChatMessage } from "../types/shared";

export type Message = ChatMessage;

export interface UpdateMessagePayload {
  content?: string;
  updatedAt?: string;
}

// Two methods to get the conversations from each sides of the conv

export async function getConversationBySender(
  userId: number,
): Promise<ChatMessage[]> {
  const { data } = await api.get<ChatMessage[]>(`/api/conversations/${userId}`);
  return data;
}

export async function getConversationByReceiver(
  userId: number,
): Promise<ChatMessage[]> {
  const { data } = await api.get<ChatMessage[]>(`/api/conversations/${userId}`);
  return data;
}

// export async function deleteMessage(messageId: number) {
//   const { data } = await api.delete(`api/messages/${messageId}`);
//   return data;
// }

// export async function editMessage(
//   messageId: number,
//   messageData: UpdateMessagePayload,
// ) {
//   const { data } = await api.patch(`api/messages/${messageId}`, messageData);
//   return data;
// }
