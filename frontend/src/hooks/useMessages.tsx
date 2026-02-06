// hooks/useMessages.ts
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";

export interface Message {
  id: number;
  senderId: number;
  content: string;
  createdAt: string;
}

export async function getMessages(channelId: number) {
  const { data } = await api.get(`/channels/${channelId}/messages`);
  return data;
}

export function useMessages(channelId: number | null) {
  return useQuery({
    queryKey: ["messages", channelId],
    queryFn: () => (channelId ? getMessages(channelId) : []),
    enabled: !!channelId,
  });
}
