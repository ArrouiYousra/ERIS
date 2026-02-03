import "../styles/messageList.css";

interface Message {
  id: number;
  content: string;
  authorId: number;
  authorName?: string;
  createdAt?: string;
}

interface MessageListProps {
  channelId?: number | null;
  conversationId?: string | null;
  messages?: Message[];
}

export function MessageList({ channelId = null, conversationId = null, messages = [] }: MessageListProps) {
  const isDM = !!conversationId;
  const hasContext = !!channelId || isDM;

  if (!hasContext) {
    return (
      <div className="message-list">
        <div className="message-list-empty">Sélectionnez un canal ou une conversation</div>
      </div>
    );
  }

  return (
    <div className="message-list">
      <div className="message-list-header">
        <h3 className="message-list-title">{isDM ? "Conversation" : "Messages"}</h3>
      </div>
      <div className="message-list-content">
        {messages.length > 0 ? (
          messages.map((message) => (
            <div key={message.id} className="message-item">
              <div className="message-item-author">{message.authorName || `User ${message.authorId}`}</div>
              <div className="message-item-content">{message.content}</div>
              {message.createdAt && (
                <div className="message-item-time">{new Date(message.createdAt).toLocaleTimeString()}</div>
              )}
            </div>
          ))
        ) : (
          <div className="message-list-empty">
            {isDM ? "Aucun message dans cette conversation." : "Aucun message pour l'instant."}
          </div>
        )}
      </div>
    </div>
  );
}
