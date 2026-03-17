import { useEffect, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useSocket } from "../api/wsApi";
import { useAuth } from "./useAuth";

export function useChannelSocket(channelId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const queryClient = useQueryClient();
  const { user } = useAuth();

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!channelId || !connected) return;

    const sub = subscribe(`/topic/channels/${channelId}`, (msg) => {
      const data = JSON.parse(msg.body);
      queryClient.setQueryData(["messages", channelId], (old: any[] = []) => {
        const exists = old.find((m) => m.id === data.id);

        if (exists) {
          return old.map((m) => (m.id === data.id ? data : m));
        } else {
          return [...old, data];
        }
      });
    });

    return () => {
      sub?.unsubscribe();
    };
  }, [channelId, connected, subscribe, queryClient]);

  // Envoyer un message
  const sendMessage = useCallback(
    (content: string) => {
      if (!channelId || !connected || !user?.id) {
        console.warn("sendMessage bloqué:", {
          channelId,
          connected,
          userId: user?.id,
        });
        return;
      }
      publish("/app/chat", { senderId: user.id, channelId, content });
    },
    [channelId, connected, user, publish],
  );

  return { sendMessage, connected };
}
