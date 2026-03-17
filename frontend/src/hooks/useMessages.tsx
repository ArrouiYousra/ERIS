// hooks/useMessages.ts
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";
import type { ChatMessage } from "../types/shared";
import {
  deleteMessage,
  editMessage,
  type UpdateMessagePayload,
} from "../api/messageApi";

export type Message = ChatMessage;

export async function getMessages(channelId: number): Promise<Message[]> {
  const { data } = await api.get<Message[]>(
    `/api/channels/${channelId}/messages`,
  );
  return data;
}

export function useMessages(channelId: number | null) {
  return useQuery({
    queryKey: ["messages", channelId],
    queryFn: () => (channelId ? getMessages(channelId) : []),
    enabled: !!channelId,
  });
}

export function useDeleteMessage(channelId: number | null) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (messageId: number) => deleteMessage(messageId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["messages", channelId] });
    },
  });
}

export function useEditMessage() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      messageId,
      payload,
    }: {
      messageId: number;
      channelId: number;
      payload: UpdateMessagePayload;
    }) => {
      return editMessage(messageId, payload);
    },
    onSuccess: (_, variables) => {
      // On utilise variables.channelId pour cibler le bon salon
      queryClient.invalidateQueries({
        queryKey: ["messages", variables.channelId],
      });
    },
  });
}
