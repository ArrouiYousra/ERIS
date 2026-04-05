// hooks/useMessages.ts
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { PrivateMessageDTO } from "../types/shared";

export async function getPrivateMessages(
  conversationId: number,
): Promise<PrivateMessageDTO[]> {
  const { data } = await api.get<PrivateMessageDTO[]>(
    `/api/conversations/${conversationId}/messages`,
  );
  return data;
}

export function usePrivateMessages(conversationId: number | null) {
  return useQuery({
    queryKey: ["messages", conversationId],
    queryFn: () => (conversationId ? getPrivateMessages(conversationId) : []),
    enabled: !!conversationId,
  });
}
