import { useEffect, useRef, useState } from "react";

export function useMessagesSocket(channelId: number | null) {
  const [messages, setMessages] = useState<any[]>([]);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    if (!channelId) return;

    const ws = new WebSocket(`ws://localhost:8081/ws/channels/${channelId}`);
    wsRef.current = ws;

    ws.onopen = () => console.log("Connected to channel", channelId);
    ws.onmessage = (event) => {
      const message = JSON.parse(event.data);
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
