import { useState } from "react";
import { Hash, Lock, Users, Search, Plus } from "lucide-react";

interface Message {
  id: number;
  content: string;
  authorId: number;
  authorName?: string;
  createdAt?: string;
}

interface MessageListProps {
  channelId?: number | null;
  channelName?: string;
  channelTopic?: string;
  isPrivate?: boolean;
  serverName?: string;
  conversationId?: string | null;
  messages?: Message[];
  showMemberList?: boolean;
  onToggleMemberList?: () => void;
}

export function MessageList({ 
  channelId = null, 
  channelName = "",
  channelTopic = "",
  isPrivate = false,
  serverName = "Serveur",
  conversationId = null, 
  messages = [],
  showMemberList = true,
  onToggleMemberList,
}: MessageListProps) {
  const [messageInput, setMessageInput] = useState("");
  const isDM = !!conversationId;
  const hasContext = !!channelId || isDM;

  if (!hasContext) {
    return (
      <div className="flex flex-col h-full bg-[#313338]">
        <div className="flex-1 flex items-center justify-center text-gray-500">
          Sélectionnez un canal ou une conversation
        </div>
      </div>
    );
  }

  const handleSendMessage = () => {
    if (!messageInput.trim()) return;
    // TODO: Implement send message
    console.log("Sending message:", messageInput);
    setMessageInput("");
  };

  return (
    <div className="flex flex-col h-full bg-[#313338]">
      {/* Channel header */}
      <div className="h-12 px-4 flex items-center gap-2 border-b border-black/20 shadow-sm shrink-0">
        {!isDM && (
          <>
            {isPrivate ? (
              <Lock className="w-5 h-5 text-gray-400 shrink-0" />
            ) : (
              <Hash className="w-5 h-5 text-gray-400 shrink-0" />
            )}
            <h3 className="text-white font-semibold truncate">
              {channelName || "général"}
            </h3>
            {channelTopic && (
              <>
                <div className="w-px h-6 bg-gray-600 mx-2 shrink-0" />
                <p className="text-gray-400 text-sm truncate flex-1" title={channelTopic}>
                  {channelTopic}
                </p>
              </>
            )}
          </>
        )}
        {isDM && (
          <h3 className="text-white font-semibold">Conversation</h3>
        )}
        
        {/* Right side of header */}
        <div className="flex items-center gap-4 ml-auto">
          {/* Members toggle */}
          {!isDM && (
            <button
              onClick={onToggleMemberList}
              className={`text-gray-400 hover:text-gray-200 transition-colors ${showMemberList ? "text-white" : ""}`}
              title={showMemberList ? "Masquer la liste des membres" : "Afficher la liste des membres"}
            >
              <Users className="w-5 h-5" />
            </button>
          )}
          
          {/* Search bar */}
          <div className="relative">
            <input
              type="text"
              placeholder={`Rechercher dans ${serverName}`}
              className="w-40 lg:w-48 px-2 py-1 pl-8 bg-[#1e1f22] rounded text-sm text-gray-300 placeholder-gray-500 outline-none focus:w-56 transition-all"
            />
            <Search className="absolute left-2 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />
          </div>
        </div>
      </div>

      {/* Messages content */}
      <div className="flex-1 overflow-y-auto">
        {messages.length > 0 ? (
          <div className="p-4 space-y-4">
            {messages.map((message) => (
              <div key={message.id} className="flex gap-3">
                <div className="w-10 h-10 rounded-full bg-[#5865F2] flex items-center justify-center text-white font-medium shrink-0">
                  {(message.authorName || "U").charAt(0).toUpperCase()}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-baseline gap-2">
                    <span className="text-white font-medium">
                      {message.authorName || `User ${message.authorId}`}
                    </span>
                    {message.createdAt && (
                      <span className="text-gray-500 text-xs">
                        {new Date(message.createdAt).toLocaleString()}
                      </span>
                    )}
                  </div>
                  <p className="text-gray-300 break-words">{message.content}</p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          /* Welcome message */
          <div className="flex flex-col items-center justify-center p-8 pt-16">
            <div className="w-[68px] h-[68px] rounded-full bg-[#5865F2] flex items-center justify-center mb-4">
              {isPrivate ? (
                <Lock className="w-10 h-10 text-white" />
              ) : (
                <Hash className="w-10 h-10 text-white" />
              )}
            </div>
            <h1 className="text-3xl font-bold text-white mb-2">
              Bienvenue sur #{channelName || "général"}
            </h1>
            <p className="text-gray-400 text-center max-w-md">
              C'est le début du salon #{channelName || "général"}. Commence à discuter !
            </p>
          </div>
        )}
      </div>

      {/* Message input */}
      <div className="px-4 pb-6 pt-2">
        <div className="flex items-center gap-2 bg-[#383a40] rounded-lg px-4 py-2">
          <button className="text-gray-400 hover:text-gray-200 transition-colors">
            <Plus className="w-6 h-6" />
          </button>
          <input
            type="text"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
            placeholder={`Envoyer un message dans #${channelName || "général"}`}
            className="flex-1 bg-transparent text-gray-200 placeholder-gray-500 outline-none"
          />
        </div>
      </div>
    </div>
  );
}
