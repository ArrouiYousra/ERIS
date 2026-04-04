import { useEffect, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useSocket } from "../api/wsApi";
import { useAuth } from "./useAuth";
import type { PrivateMessageDTO } from "../types/shared";

export function usePrivateMessageSocket(conversationId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const queryClient = useQueryClient();
  const { user } = useAuth();

  useEffect(() => {
    if (!conversationId || !connected) return;

    const sub = subscribe(`/topic/conversation/${conversationId}`, (msg) => {
      const event = JSON.parse(msg.body) as {
        type: string;
        data: PrivateMessageDTO | number;
      };

      queryClient.setQueryData(
        ["messages", conversationId],
        (old: PrivateMessageDTO[] = []) => {
          switch (event.type) {
            case "NEW": {
              const newMsg = event.data as PrivateMessageDTO;
              const exists = old.find((m) => m.messageId === newMsg.messageId);
              return exists ? old : [...old, newMsg];
            }
            case "EDIT": {
              const edited = event.data as PrivateMessageDTO;
              return old.map((m) =>
                m.messageId === edited.messageId ? edited : m,
              );
            }
            case "DELETE": {
              const deletedId = event.data as number;
              return old.filter((m) => m.messageId !== deletedId);
            }
            default:
              return old;
          }
        },
      );

      if (event.type === "NEW" || event.type === "DELETE") {
        queryClient.invalidateQueries({ queryKey: ["conversations"] });
      }
    });

    return () => sub?.unsubscribe();
  }, [conversationId, connected, subscribe, queryClient]);

  // Envoi via WebSocket au lieu de HTTP
  const sendMessage = useCallback(
    (content: string) => {
      if (!conversationId || !connected || !user?.id) return;
      publish("/app/private.send", { conversationId, content });
    },
    [conversationId, connected, user, publish],
  );

  return { sendMessage, connected };
}
