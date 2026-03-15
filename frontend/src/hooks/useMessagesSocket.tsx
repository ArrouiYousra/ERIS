import { useEffect, useRef, useState } from "react";
import type { Message } from "./useMessages";

const WS_BASE_URL = import.meta.env.VITE_WS_BASE_URL || "ws://localhost:8081";

export function useMessagesSocket(channelId: number | null) {
  const [messages, setMessages] = useState<Message[]>([]);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    if (!channelId) return;

    const ws = new WebSocket(`${WS_BASE_URL}/ws/channels/${channelId}`);
    wsRef.current = ws;

    ws.onopen = () => console.log("Connected to channel", channelId);
    ws.onmessage = (event) => {
      const message = JSON.parse(event.data) as Message;
      setMessages((prev) => [...prev, message]);
    };
    ws.onclose = () => console.log("Disconnected from channel", channelId);

    return () => {
      ws.close();
      wsRef.current = null;
    };
  }, [channelId]);

  const sendMessage = (content: string) => {
    if (wsRef.current) {
      wsRef.current.send(JSON.stringify({ content }));
    }
  };

  return { messages, sendMessage };
}
