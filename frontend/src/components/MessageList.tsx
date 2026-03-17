import { useState } from "react";
import {
  Hash,
  Lock,
  Search,
  Plus,
  LogOut,
  Pencil,
  Settings,
  SlidersHorizontal,
  ArrowLeft,
} from "lucide-react";
import { useMessages } from "../hooks/useMessages";
import type { Message } from "../hooks/useMessages";
import { useChannelSocket } from "../hooks/useChannelSocket";
import { useTyping } from "../hooks/useTyping";
import { editMessage } from "../api/messageApi";
import { useAuth } from "../hooks/useAuth";

interface MessageListProps {
  channelId?: number | null;
  serverId?: number | null;
  channelName?: string;
  channelTopic?: string;
  isPrivate?: boolean;
  serverName?: string;
  conversationId?: string | null;
  onToggleSidebar?: () => void;
  onLeaveServer?: () => Promise<void> | void;
}

export function MessageList({
  channelId = null,
  serverId = null,
  channelName = "",
  channelTopic = "",
  isPrivate = false,
  serverName = "Serveur",
  conversationId = null,
  onToggleSidebar,
  onLeaveServer,
}: MessageListProps) {
  const [messageInput, setMessageInput] = useState("");
  const { user } = useAuth();
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValue, setEditValue] = useState<string>("");
  const [showFutureCard, setShowFutureCard] = useState(false);
  const { data: messages = [] } = useMessages(channelId ?? null);
  const { sendMessage } = useChannelSocket(channelId ?? null);
  const { typingText, onInputChange, stopTyping } = useTyping(
    channelId ?? null,
  );

  const handleUpdateMessage = async (messageId: number) => {
    if (!editValue.trim()) return;

    try {
      // On appelle l'API pour modifier en base de données
      await editMessage(messageId, { content: editValue });
      // Une fois fini, on remet editingId à null pour repasser en mode lecture
      setEditingId(null);
    } catch (error) {
      console.error("Erreur lors de la modif :", error);
    }
  };

  const isDM = !!conversationId;
  const hasContext = !!channelId || isDM;

  if (!hasContext && serverId) {
    return (
      <div className="flex flex-col h-full bg-[#313338]">
        <div className="h-12 px-2 sm:px-4 flex items-center gap-2 border-b border-black/20 shadow-sm shrink-0 min-w-0">
          <h3 className="text-white font-semibold truncate">{serverName}</h3>
          <button
            type="button"
            onClick={() => setShowFutureCard((open) => !open)}
            className="ml-auto inline-flex items-center justify-center rounded-md px-2 py-1.5 bg-[#4a4d55] text-gray-100 hover:bg-[#5865F2] transition-colors"
            title="Ouvrir la carte serveur"
            aria-label="Ouvrir la carte serveur"
          >
            <SlidersHorizontal className="w-4 h-4" />
          </button>
        </div>
        <div className="flex-1 flex items-center justify-center p-5">
          <div className="w-full max-w-xl rounded-xl border border-white/10 bg-[#2b2d31] p-5 sm:p-6 shadow-lg">
            <h2 className="text-xl sm:text-2xl font-bold text-white mb-2">
              {serverName}
            </h2>
            <p className="text-sm text-gray-300 mb-5">
              Clique sur un salon dans la colonne de gauche pour voir les
              messages.
            </p>
            <div className="flex flex-col sm:flex-row gap-3">
              <button
                type="button"
                onClick={() => onLeaveServer?.()}
                className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-md bg-red-500/85 hover:bg-red-500 text-white text-sm font-medium transition-colors"
              >
                <LogOut className="w-4 h-4" />
                Quitter le serveur
              </button>
              <button
                type="button"
                disabled
                className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-md bg-[#404249] text-[#c7c9ce] text-sm font-medium cursor-not-allowed"
                title="Les options arriveront bientôt"
              >
                <Settings className="w-4 h-4" />
                Options (bientot)
              </button>
            </div>
            {showFutureCard && (
              <div className="mt-4 rounded-lg border border-white/10 bg-[#1f2125] p-4">
                <p className="text-sm text-gray-300">
                  Carte serveur a venir : options et parametres seront ajoutes
                  ici.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

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
    sendMessage(messageInput);
    setMessageInput("");
    stopTyping();
  };

  return (
    <div className="flex flex-col h-full bg-[#313338]">
      {/* Channel header */}
      <div className="h-12 px-2 sm:px-4 flex items-center gap-2 border-b border-black/20 shadow-sm shrink-0 min-w-0">
        {!isDM && (
          <button
            type="button"
            onClick={() => onToggleSidebar?.()}
            className="chat-mobile-back-button inline-flex items-center justify-center rounded-md p-1.5 text-gray-300 hover:text-white hover:bg-[#404249] transition-colors"
            aria-label="Retour aux salons"
            title="Retour aux salons"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
        )}
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
                <p
                  className="text-gray-400 text-sm truncate flex-1 hidden md:block"
                  title={channelTopic}
                >
                  {channelTopic}
                </p>
              </>
            )}
          </>
        )}
        {isDM && <h3 className="text-white font-semibold">Conversation</h3>}

        {/* Right side of header */}
        <div className="flex items-center gap-2 sm:gap-4 ml-auto">
          <div className="relative hidden sm:block">
            <input
              type="text"
              placeholder={`Rechercher dans ${serverName}`}
              className="w-36 lg:w-48 px-2 py-1 pl-8 bg-[#1e1f22] rounded text-sm text-gray-300 placeholder-gray-500 outline-none focus:w-52 transition-all"
            />
            <Search className="absolute left-2 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />
          </div>
        </div>
      </div>

      {/* Messages content */}
      <div className="flex-1 overflow-y-auto">
        {messages.length > 0 ? (
          <div className="p-4 space-y-4">
            {messages.map((message: Message) => (
              <div key={message.id} className="flex gap-3">
                <div className="w-10 h-10 rounded-full bg-[#5865F2] flex items-center justify-center text-white font-medium shrink-0">
                  {(message.senderUsername || "U").charAt(0).toUpperCase()}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-baseline gap-2">
                    <span className="text-white font-medium">
                      {message.senderUsername ||
                        `Utilisateur ${message.senderId}`}
                    </span>
                    {message.createdAt && (
                      <span className="text-gray-500 text-xs">
                        {new Date(message.createdAt).toLocaleString()}
                      </span>
                    )}
                  </div>
                  {editingId === message.id ? (
                    /* Mode ÉDITION : On affiche l'input */
                    <div className="flex flex-col gap-2 ml-auto">
                      <input
                        type="text"
                        value={editValue}
                        onChange={(e) => setEditValue(e.target.value)}
                        className="bg-[#383a40] text-gray-200 p-2 rounded outline-none border border-[#5865F2]"
                        autoFocus
                        onKeyDown={(e) => {
                          if (e.key === "Enter") {
                            handleUpdateMessage(message.id);
                          } else if (e.key === "Escape") {
                            setEditingId(null);
                          }
                        }}
                      />
                      <div className="flex gap-2 text-xs">
                        <span className="text-gray-400">
                          Échap pour annuler • Entrée pour enregistrer
                        </span>
                      </div>
                    </div>
                  ) : (
                    /* Mode LECTURE : On affiche le texte normal */
                    <p className="text-gray-300 break-words">
                      {message.content}
                    </p>
                  )}
                </div>
                {message.senderId === user?.id && !editingId && (
                  <div className="input ">
                    <button
                      className="text-gray-400 hover:text-gray-200 transition-colors opacity-0 group-hover:opacity-100 p-1"
                      onClick={() => {
                        setEditingId(message.id);
                        setEditValue(message.content);
                      }}
                    >
                      <Pencil className="w-4 h-4" />
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
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
              C'est le début du salon #{channelName || "général"}. Commence à
              discuter !
            </p>
          </div>
        )}

        {/* Typing indicator */}
        {typingText && (
          <div className="px-4 pb-2 text-sm text-gray-400 italic">
            {typingText}
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
            onChange={(e) => {
              setMessageInput(e.target.value);
              if (e.target.value.length > 0) onInputChange();
            }}
            onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
            placeholder={`Envoyer un message dans #${channelName || "général"}`}
            className="flex-1 bg-transparent text-gray-200 placeholder-gray-500 outline-none"
          />
        </div>
      </div>
    </div>
  );
}
