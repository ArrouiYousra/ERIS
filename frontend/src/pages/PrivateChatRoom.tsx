import { useState, useRef, useEffect } from "react";
import {
  ArrowLeft,
  Search,
  Phone,
  Video,
  //   MoreVertical,
  Plus,
  Pencil,
  Trash2,
  Check,
  X,
} from "lucide-react";
import { useAuth } from "../hooks/useAuth";
import {
  useConversationMessages,
  useSendMessage,
  useEditMessage,
  useDeleteMessage,
} from "../hooks/useConversations";
import type { PrivateMessageDTO } from "../types/shared";

// ─── Types ────────────────────────────────────────────────────────────────────

interface PrivateMessage {
  id: number;
  conversationId: number;
  senderId: number;
  senderUsername: string;
  senderAvatarUrl?: string | null;
  content: string;
  createdAt: string;
  updatedAt?: string | null;
}

// ─── Props ────────────────────────────────────────────────────────────────────

interface PrivateChatRoomProps {
  conversationId: number;
  otherUserId: number;
  otherUsername: string;
  otherUserAvatarUrl?: string | null;
  onBack?: () => void; // bouton retour mobile
}

// ─── Mapper DTO → UI ─────────────────────────────────────────────────────────

function mapMessageDTOToUIMessage(dto: PrivateMessageDTO): PrivateMessage {
  return {
    id: dto.messageId,
    conversationId: dto.conversationId,
    senderId: dto.sender.userId,
    senderUsername: dto.sender.username,
    senderAvatarUrl: null, // Pas encore d'avatar dans le DTO
    content: dto.content,
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
  };
}

// ─── Utilitaires ──────────────────────────────────────────────────────────────

function formatMessageTime(iso: string): string {
  const date = new Date(iso);
  return date.toLocaleTimeString("fr-FR", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatDateSeparator(iso: string): string {
  const date = new Date(iso);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(today.getDate() - 1);

  if (date.toDateString() === today.toDateString()) return "Aujourd'hui";
  if (date.toDateString() === yesterday.toDateString()) return "Hier";
  return date.toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });
}

function isSameDay(a: string, b: string): boolean {
  return new Date(a).toDateString() === new Date(b).toDateString();
}

function getInitial(name: string): string {
  return (name || "?").charAt(0).toUpperCase();
}

// ─── Avatar ───────────────────────────────────────────────────────────────────

function Avatar({
  username,
  avatarUrl,
  size = 40,
}: {
  username: string;
  avatarUrl?: string | null;
  size?: number;
}) {
  return avatarUrl ? (
    <img
      src={avatarUrl}
      alt={username}
      className="rounded-full object-cover shrink-0"
      style={{ width: size, height: size }}
    />
  ) : (
    <div
      className="rounded-full bg-[#5865F2] flex items-center justify-center text-white font-semibold select-none shrink-0"
      style={{ width: size, height: size, fontSize: size * 0.4 }}
    >
      {getInitial(username)}
    </div>
  );
}

// ─── DateSeparator ────────────────────────────────────────────────────────────

function DateSeparator({ label }: { label: string }) {
  return (
    <div className="flex items-center gap-3 px-4 py-2 select-none">
      <div className="flex-1 h-px bg-white/10" />
      <span className="text-xs text-gray-500 font-medium">{label}</span>
      <div className="flex-1 h-px bg-white/10" />
    </div>
  );
}

// ─── MessageBubble ────────────────────────────────────────────────────────────

function MessageBubble({
  message,
  isOwn,
  isEditing,
  editValue,
  onEditChange,
  onEditSubmit,
  onEditCancel,
  onStartEdit,
  onDelete,
  showAvatar,
  otherUserAvatarUrl,
}: {
  message: PrivateMessage;
  isOwn: boolean;
  isEditing: boolean;
  editValue: string;
  onEditChange: (v: string) => void;
  onEditSubmit: () => void;
  onEditCancel: () => void;
  onStartEdit: () => void;
  onDelete: () => void;
  showAvatar: boolean;
  otherUserAvatarUrl?: string | null;
}) {
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isEditing) inputRef.current?.focus();
  }, [isEditing]);

  return (
    <div
      className={`group flex items-end gap-2 px-4 py-0.5 ${
        isOwn ? "flex-row-reverse" : "flex-row"
      }`}
    >
      {/* Avatar — affiché seulement sur le dernier message d'un bloc */}
      <div className="w-8 shrink-0">
        {showAvatar && !isOwn && (
          <Avatar
            username={message.senderUsername}
            avatarUrl={otherUserAvatarUrl}
            size={32}
          />
        )}
      </div>

      <div
        className={`flex flex-col max-w-[75%] sm:max-w-[60%] ${
          isOwn ? "items-end" : "items-start"
        }`}
      >
        {/* Nom de l'expéditeur (premier message du bloc) */}
        {showAvatar && !isOwn && (
          <span className="text-xs font-semibold text-gray-400 mb-1 px-1">
            {message.senderUsername}
          </span>
        )}

        {/* Bulle */}
        {isEditing ? (
          <div className="flex flex-col gap-1.5 w-full">
            <input
              ref={inputRef}
              type="text"
              value={editValue}
              onChange={(e) => onEditChange(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") onEditSubmit();
                if (e.key === "Escape") onEditCancel();
              }}
              className="bg-[#383a40] text-gray-200 px-3 py-2 rounded-lg outline-none border border-[#5865F2] text-sm w-full"
            />
            <div className="flex items-center gap-2 text-[11px] text-gray-500">
              <span>Échap pour annuler</span>
              <span>•</span>
              <span>Entrée pour enregistrer</span>
              <button
                onClick={onEditSubmit}
                className="ml-auto p-1 rounded bg-[#5865F2] hover:bg-[#4752c4] text-white transition-colors"
              >
                <Check className="w-3 h-3" />
              </button>
              <button
                onClick={onEditCancel}
                className="p-1 rounded bg-[#404249] hover:bg-[#4a4d55] text-gray-300 transition-colors"
              >
                <X className="w-3 h-3" />
              </button>
            </div>
          </div>
        ) : (
          <div className="relative group/bubble flex items-end gap-1">
            {/* Actions rapides (hover) — côté gauche pour messages propres */}
            {isOwn && (
              <div className="flex items-center gap-1 opacity-0 group-hover/bubble:opacity-100 transition-opacity order-first">
                <button
                  onClick={onStartEdit}
                  className="p-1 rounded hover:bg-[#404249] text-gray-500 hover:text-gray-200 transition-colors"
                  title="Modifier"
                >
                  <Pencil className="w-3.5 h-3.5" />
                </button>
                <button
                  onClick={onDelete}
                  className="p-1 rounded hover:bg-red-500/20 text-gray-500 hover:text-red-400 transition-colors"
                  title="Supprimer"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            )}

            <div
              className={`px-3 py-2 rounded-2xl text-sm leading-relaxed break-words ${
                isOwn
                  ? "bg-[#5865F2] text-white rounded-br-sm"
                  : "bg-[#383a40] text-gray-200 rounded-bl-sm"
              }`}
            >
              {message.content}
            </div>
          </div>
        )}

        {/* Timestamp */}
        <span className="text-[10px] text-gray-600 mt-0.5 px-1 tabular-nums">
          {formatMessageTime(message.createdAt)}
        </span>
      </div>
    </div>
  );
}

// ─── PrivateChatRoom ──────────────────────────────────────────────────────────

export function PrivateChatRoom({
  conversationId,
  otherUsername,
  otherUserAvatarUrl,
  onBack,
}: PrivateChatRoomProps) {
  const { user } = useAuth();
  const currentUserId = user?.id ?? 0;

  const [input, setInput] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValue, setEditValue] = useState("");
  const [showSearch, setShowSearch] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  const bottomRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Hooks React Query
  const { data: messagesDTO = [], isLoading: loading } =
    useConversationMessages(conversationId);
  const sendMessageMutation = useSendMessage();
  const editMessageMutation = useEditMessage();
  const deleteMessageMutation = useDeleteMessage();

  // Transforme les DTOs en format UI
  const messages = messagesDTO.map(mapMessageDTOToUIMessage);

  // Scroll automatique vers le bas quand les messages changent
  useEffect(() => {
    if (!loading) {
      bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, loading]);

  // Focus input à l'ouverture
  useEffect(() => {
    inputRef.current?.focus();
  }, [conversationId]);

  // ── Envoi ──
  const handleSend = async () => {
    const trimmed = input.trim();
    if (!trimmed) return;
    setInput("");
    try {
      await sendMessageMutation.mutateAsync({
        conversationId,
        content: trimmed,
      });
    } catch (err) {
      console.error("Erreur envoi :", err);
    }
  };

  // ── Édition ──
  const handleStartEdit = (msg: PrivateMessage) => {
    setEditingId(msg.id);
    setEditValue(msg.content);
  };

  const handleEditSubmit = async () => {
    if (!editingId || !editValue.trim()) return;
    try {
      await editMessageMutation.mutateAsync({
        messageId: editingId,
        conversationId,
        content: editValue,
      });
      setEditingId(null);
    } catch (err) {
      console.error("Erreur édition :", err);
    }
  };

  // ── Suppression ──
  const handleDelete = async (messageId: number) => {
    const confirmed = window.confirm("Supprimer ce message ?");
    if (!confirmed) return;
    try {
      await deleteMessageMutation.mutateAsync({ messageId, conversationId });
    } catch (err) {
      console.error("Erreur suppression :", err);
    }
  };

  // ── Filtrage recherche ──
  const displayedMessages = searchQuery
    ? messages.filter((m) =>
        m.content.toLowerCase().includes(searchQuery.toLowerCase()),
      )
    : messages;

  // ── Regroupement par expéditeur pour éviter les avatars répétés ──
  function isLastOfGroup(index: number): boolean {
    const next = displayedMessages[index + 1];
    return !next || next.senderId !== displayedMessages[index].senderId;
  }

  return (
    <div className="flex flex-col flex-1 min-h-0 bg-[#313338]">
      {/* ── Header ── */}
      <div className="h-12 px-3 sm:px-4 flex items-center gap-2 border-b border-black/20 shadow-sm shrink-0">
        {/* Retour mobile */}
        {onBack && (
          <button
            type="button"
            onClick={onBack}
            className="md:hidden inline-flex items-center justify-center rounded-md p-1.5 text-gray-400 hover:text-white hover:bg-[#404249] transition-colors"
            aria-label="Retour"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
        )}

        <Avatar
          username={otherUsername}
          avatarUrl={otherUserAvatarUrl}
          size={28}
        />
        <h3 className="text-white font-semibold text-sm truncate flex-1">
          {otherUsername}
        </h3>

        {/* Actions header */}
        <div className="flex items-center gap-1 ml-auto">
          <button
            type="button"
            className="p-1.5 rounded-md text-gray-400 hover:text-gray-200 hover:bg-[#404249] transition-colors"
            title="Appel vocal (bientôt)"
            disabled
          >
            <Phone className="w-4 h-4" />
          </button>
          <button
            type="button"
            className="p-1.5 rounded-md text-gray-400 hover:text-gray-200 hover:bg-[#404249] transition-colors"
            title="Appel vidéo (bientôt)"
            disabled
          >
            <Video className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => {
              setShowSearch((v) => !v);
              setSearchQuery("");
            }}
            className={`p-1.5 rounded-md transition-colors ${
              showSearch
                ? "text-white bg-[#404249]"
                : "text-gray-400 hover:text-gray-200 hover:bg-[#404249]"
            }`}
            title="Rechercher dans la conversation"
          >
            <Search className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* ── Barre de recherche inline ── */}
      {showSearch && (
        <div className="px-4 py-2 border-b border-black/20 bg-[#2b2d31]">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 pointer-events-none" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Rechercher dans la conversation…"
              autoFocus
              className="w-full bg-[#1e1f22] text-gray-300 text-sm placeholder-gray-500 rounded-md pl-9 pr-4 py-2 outline-none focus:ring-1 focus:ring-[#5865F2]/50 transition"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery("")}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-200"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
          {searchQuery && (
            <p className="text-xs text-gray-600 mt-1 px-1">
              {displayedMessages.length} résultat
              {displayedMessages.length !== 1 ? "s" : ""}
            </p>
          )}
        </div>
      )}

      {/* ── Zone messages ── */}
      <div className="flex-1 overflow-y-auto py-4">
        {loading ? (
          <div className="flex items-center justify-center h-full text-gray-500 text-sm">
            Chargement…
          </div>
        ) : displayedMessages.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full p-8 text-center">
            <Avatar
              username={otherUsername}
              avatarUrl={otherUserAvatarUrl}
              size={64}
            />
            <h2 className="text-white font-bold text-xl mt-4 mb-1">
              {otherUsername}
            </h2>
            <p className="text-gray-400 text-sm max-w-xs">
              {searchQuery
                ? "Aucun message ne correspond à ta recherche."
                : `C'est le début de ta conversation avec ${otherUsername}. Dis bonjour !`}
            </p>
          </div>
        ) : (
          <div className="space-y-0">
            {displayedMessages.map((message, index) => {
              const prev = displayedMessages[index - 1];
              const showDateSep =
                !prev || !isSameDay(prev.createdAt, message.createdAt);
              const isLastGroup = isLastOfGroup(index);
              const isOwn = message.senderId === currentUserId;

              return (
                <div key={message.id}>
                  {showDateSep && (
                    <DateSeparator
                      label={formatDateSeparator(message.createdAt)}
                    />
                  )}
                  <MessageBubble
                    message={message}
                    isOwn={isOwn}
                    isEditing={editingId === message.id}
                    editValue={editValue}
                    onEditChange={setEditValue}
                    onEditSubmit={handleEditSubmit}
                    onEditCancel={() => setEditingId(null)}
                    onStartEdit={() => handleStartEdit(message)}
                    onDelete={() => handleDelete(message.id)}
                    showAvatar={isLastGroup}
                    otherUserAvatarUrl={otherUserAvatarUrl}
                  />
                </div>
              );
            })}
            <div ref={bottomRef} />
          </div>
        )}
      </div>

      {/* ── Zone de saisie ── */}
      <div className="px-4 pb-5 pt-2 shrink-0">
        <div className="flex items-center gap-2 bg-[#383a40] rounded-xl px-3 py-2">
          <button
            type="button"
            className="text-gray-400 hover:text-gray-200 transition-colors shrink-0"
            title="Ajouter un fichier (bientôt)"
            disabled
          >
            <Plus className="w-5 h-5" />
          </button>
          <input
            ref={inputRef}
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && !e.shiftKey && handleSend()}
            placeholder={`Message @${otherUsername}`}
            className="flex-1 bg-transparent text-gray-200 placeholder-gray-500 outline-none text-sm min-w-0"
          />
          {input.trim() && (
            <button
              type="button"
              onClick={handleSend}
              className="shrink-0 p-1.5 rounded-lg bg-[#5865F2] hover:bg-[#4752c4] text-white transition-colors"
              title="Envoyer"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
                className="w-4 h-4"
              >
                <path d="M3.478 2.405a.75.75 0 00-.926.94l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.405z" />
              </svg>
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
