// hooks/useMessages.ts
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { Conversation } from "../types/shared";

export async function getConversations(
  userId: number,
): Promise<Conversation[]> {
  const { data } = await api.get<Conversation[]>(
    `/api/channels/${userId}/messages`,
  );
  return data;
}

export function useConversations(userId: number | null) {
  return useQuery({
    queryKey: ["conversations", userId],
    queryFn: () => (userId ? getConversations(userId) : []),
    enabled: !!userId,
  });
}
