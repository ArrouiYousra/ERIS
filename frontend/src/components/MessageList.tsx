import { useState } from "react";
import { useMessages } from "../hooks/useMessages";

interface MessageListProps {
  channelId: number | null;
}

export function MessageList({ channelId }: MessageListProps) {
  const { data: messages = [], isLoading } = useMessages(channelId); // your fetch hook
  const [input, setInput] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    // Call your API / WebSocket send function
    sendMessage(input);

    setInput(""); // clear input after sending
  };

  return (
    <div className="chat-messages flex-1 flex flex-col justify-between">
      {/* Messages */}
      <div className="messages-list flex-1 overflow-y-auto p-4">
        {isLoading ? (
          <p>Loading messages...</p>
        ) : messages.length === 0 ? (
          <p className="text-gray-400">No messages yet</p>
        ) : (
          messages.map((m: any) => (
            <div key={m.id} className="message">
              <strong>{m.senderUsername}</strong>: {m.content}
            </div>
          ))
        )}
      </div>

      {/* Always render input bar */}
      <div className="chat-input p-4 border-t border-gray-600">
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Type your message..."
            className="w-full p-2 rounded bg-gray-700 text-white"
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />
        </form>
      </div>
    </div>
  );
}
