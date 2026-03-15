// hooks/useMessages.ts
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { ChatMessage } from "../types/shared";

export interface Message {
  id: number;
  senderId: number;
  senderUsername?: string;
  content: string;
  createdAt: string;
}

export async function getMessages(channelId: number): Promise<Message[]> {
  const { data } = await api.get<Message[]>(`/api/channels/${channelId}/messages`);
  return data;
}

export function useMessages(channelId: number | null) {
  return useQuery({
    queryKey: ["messages", channelId],
    queryFn: () => (channelId ? getMessages(channelId) : []),
    enabled: !!channelId,
  });
}
