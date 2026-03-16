import { useEffect, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useSocket } from "../api/wsApi";
import { useAuth } from "./useAuth";

interface RealtimeMessage {
  id: number;
  senderId: number;
  content: string;
  createdAt: string;
  senderUsername?: string;
}

export function useChannelSocket(channelId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const userId = user?.id ?? null;

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!channelId || !connected) return;

    const sub = subscribe(`/topic/channels/${channelId}`, (msg) => {
      const data = JSON.parse(msg.body);
      queryClient.setQueryData<RealtimeMessage[]>(
        ["messages", channelId],
        (old = []) => {
        const exists = old.find((m) => m.id === data.id);

        if (exists) {
          return old.map((m) => (m.id === data.id ? data : m));
        } else {
          return [...old, data];
        }
        },
      );
    });

    return () => {
      sub?.unsubscribe();
    };
  }, [channelId, connected, subscribe, queryClient]);

  // Envoyer un message
  const sendMessage = useCallback(
    (content: string) => {
      if (!channelId || !connected || !userId) {
        console.warn("sendMessage bloque:", { channelId, connected, userId });
        return;
      }
      publish("/app/chat", { senderId: userId, channelId, content });
    },
    [channelId, connected, userId, publish],
  );

  return { sendMessage, connected };
}