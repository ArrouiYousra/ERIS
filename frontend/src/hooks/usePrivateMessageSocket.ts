import { useEffect, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useSocket } from "../api/wsApi";
import { useAuth } from "./useAuth";
import type { PrivateMessageDTO, PrivateMessageEvent } from "../types/shared";

export function usePrivateMessageSocket(conversationId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const queryClient = useQueryClient();
  const { user } = useAuth();

  useEffect(() => {
    console.log("[WebSocket] useEffect déclenché:", {
      conversationId,
      connected,
    });

    if (!conversationId || !connected) {
      console.log("[WebSocket] Non connecté ou pas de conversationId", {
        conversationId,
        connected,
      });
      return;
    }

    console.log(
      "[WebSocket] Tentative d'abonnement à la conversation:",
      conversationId,
    );
    const sub = subscribe(`/topic/conversation/${conversationId}`, (msg) => {
      console.log("[WebSocket] 📨 Message brut reçu:", msg.body);

      const event = JSON.parse(msg.body) as PrivateMessageEvent;
      console.log("[WebSocket] Event parsé:", event);

      queryClient.setQueryData(
        ["messages", conversationId],
        (old: PrivateMessageDTO[] = []) => {
          console.log("[WebSocket] Cache avant update:", old);

          switch (event.type) {
            case "NEW": {
              const exists = old.some(
                (m) => m.messageId === event.data.messageId,
              );
              return exists ? old : [...old, event.data];
            }
            case "EDIT":
              return old.map((m) =>
                m.messageId === event.data.messageId ? event.data : m,
              );
            case "DELETE":
              return old.filter((m) => m.messageId !== event.data);
          }
        },
      );

      if (event.type === "NEW" || event.type === "DELETE") {
        queryClient.invalidateQueries({ queryKey: ["conversations"] });
      }
    });

    return () => {
      console.log(
        "[WebSocket] Désabonnement de la conversation:",
        conversationId,
      );
      sub?.unsubscribe();
    };
  }, [conversationId, connected, subscribe, queryClient]);

  // Envoi via WebSocket au lieu de HTTP
  const sendMessage = useCallback(
    (content: string) => {
      if (!conversationId || !connected || !user?.id) {
        console.warn("[WebSocket] Impossible d'envoyer:", {
          conversationId,
          connected,
          userId: user?.id,
        });
        return;
      }
      console.log("[WebSocket] Envoi message:", { conversationId, content });
      publish("/app/private.chat", { conversationId, content });
    },
    [conversationId, connected, user, publish],
  );

  return { sendMessage, connected };
}
