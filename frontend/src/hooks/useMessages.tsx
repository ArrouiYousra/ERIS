// hooks/useMessages.ts
import { useQuery, useQueryClient, useMutation } from "@tanstack/react-query";
import { api } from "../api/client";
import { deleteMessage } from "../api/messageApi";

export interface Message {
  id: number;
  senderId: number;
  content: string;
  createdAt: string;
}

export async function getMessages(channelId: number) {
  const { data } = await api.get(`/api/channels/${channelId}/messages`);
  return data;
}

export function useDeleteMessage(channelId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteMessage(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["messages", channelId] });
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    },
  });
}

export function useMessages(channelId: number | null) {
  return useQuery({
    queryKey: ["messages", channelId],
    queryFn: () => (channelId ? getMessages(channelId) : []),
    enabled: !!channelId,
  });
}
