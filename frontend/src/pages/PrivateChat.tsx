import { useState } from "react";
import { Plus, X, MessageCircle } from "lucide-react";
import { UserBar } from "../components/friends/UserBar";
import { DMRows } from "../components/friends";
import { RightPanel } from "../components/friends";
import { PrivateChatRoom } from "../pages/PrivateChatRoom";
import { useServerSocket } from "../hooks/useServerSocket";
import {
  useConversations,
  useDeleteConversation,
  useCreateConversation,
} from "../hooks/useConversations";
import type { ConversationPreviewDTO } from "../types/shared";
import "../styles/chat.css";
import { useAuth } from "../hooks/useAuth";

// ─── Types adaptés pour le composant ──────────────────────────────────────────

interface ConversationPreview {
  id: number;
  otherUserId: number;
  otherUsername: string;
  otherUserAvatarUrl?: string | null;
  lastMessageContent?: string | null;
  lastMessageAt?: string | null;
  unreadCount?: number;
}

// ─── Fonction utilitaire pour transformer le DTO ──────────────────────────────

function mapConversationDTOToPreview(
  dto: ConversationPreviewDTO,
  currentUserId: number | null,
): ConversationPreview {
  // Si on n'a pas l'ID de l'utilisateur courant, on prend le premier participant
  // (normalement ça ne devrait pas arriver si l'API renvoie bien les conversations de l'utilisateur)
  const otherParticipant = currentUserId
    ? dto.participants.find((p) => p.userId !== currentUserId)
    : dto.participants[0];

  return {
    id: dto.conversationId,
    otherUserId: otherParticipant?.userId ?? 0,
    otherUsername: otherParticipant?.username ?? "Utilisateur",
    otherUserAvatarUrl: null, // Le backend ne fournit pas encore d'avatar
    lastMessageContent: dto.lastPrivateMessage?.content ?? null,
    lastMessageAt: dto.lastPrivateMessage?.createdAt ?? null,
    unreadCount: 0, // À implémenter côté backend si besoin
  };
}

// ─── Utilitaires ──────────────────────────────────────────────────────────────

function formatPreviewTime(iso?: string | null): string {
  if (!iso) return "";
  const date = new Date(iso);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMin = Math.floor(diffMs / 60000);
  if (diffMin < 1) return "maintenant";
  if (diffMin < 60) return `il y a ${diffMin}m`;
  const diffH = Math.floor(diffMin / 60);
  if (diffH < 24) return `il y a ${diffH}h`;
  return date.toLocaleDateString("fr-FR", { day: "2-digit", month: "2-digit" });
}

function getInitial(name: string): string {
  return (name || "?").charAt(0).toUpperCase();
}

// ─── Avatar ───────────────────────────────────────────────────────────────────

function Avatar({
  username,
  avatarUrl,
  size = 40,
  unread = 0,
}: {
  username: string;
  avatarUrl?: string | null;
  size?: number;
  unread?: number;
}) {
  return (
    <div className="relative shrink-0" style={{ width: size, height: size }}>
      {avatarUrl ? (
        <img
          src={avatarUrl}
          alt={username}
          className="rounded-full object-cover w-full h-full"
        />
      ) : (
        <div
          className="rounded-full bg-[#5865F2] flex items-center justify-center text-white font-semibold select-none"
          style={{ width: size, height: size, fontSize: size * 0.4 }}
        >
          {getInitial(username)}
        </div>
      )}
      {unread > 0 && (
        <span className="absolute -bottom-0.5 -right-0.5 min-w-[16px] h-4 px-1 rounded-full bg-red-500 text-white text-[10px] font-bold flex items-center justify-center leading-none border-2 border-[#2b2d31]">
          {unread > 9 ? "9+" : unread}
        </span>
      )}
    </div>
  );
}

// ─── ConversationRow ──────────────────────────────────────────────────────────

function ConversationRow({
  conversation,
  isSelected,
  onSelect,
  onClose,
}: {
  conversation: ConversationPreview;
  isSelected: boolean;
  onSelect: () => void;
  onClose: (e: React.MouseEvent) => void;
}) {
  return (
    <div
      onClick={onSelect}
      className={`group relative flex items-center gap-3 px-2 py-2 mx-2 rounded-lg cursor-pointer transition-colors ${
        isSelected
          ? "bg-[#404249] text-white"
          : "text-gray-400 hover:bg-[#35373c] hover:text-gray-200"
      }`}
    >
      <Avatar
        username={conversation.otherUsername}
        avatarUrl={conversation.otherUserAvatarUrl}
        size={40}
        unread={conversation.unreadCount}
      />

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between gap-1">
          <span
            className={`text-sm font-semibold truncate ${
              isSelected ? "text-white" : "text-gray-200"
            } ${conversation.unreadCount ? "font-bold" : ""}`}
          >
            {conversation.otherUsername}
          </span>
          <span className="text-[10px] text-gray-500 shrink-0 tabular-nums">
            {formatPreviewTime(conversation.lastMessageAt)}
          </span>
        </div>
        <p
          className={`text-xs truncate mt-0.5 ${
            conversation.unreadCount
              ? "text-gray-200 font-medium"
              : "text-gray-500"
          }`}
        >
          {conversation.lastMessageContent ?? "Aucun message"}
        </p>
      </div>

      {/* Bouton fermer visible au hover */}
      <button
        onClick={onClose}
        className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity p-1 rounded hover:bg-[#4a4d55] text-gray-400 hover:text-gray-200"
        title="Fermer la conversation"
      >
        <X className="w-3.5 h-3.5" />
      </button>
    </div>
  );
}

// ─── DMSidebar ────────────────────────────────────────────────────────────────

function DMSidebar({
  conversations,
  selectedId,
  onSelect,
  onClose,
  searchQuery,
  isLoading,
}: {
  conversations: ConversationPreview[];
  selectedId: number | null;
  onSelect: (id: number) => void;
  onClose: (id: number) => void;
  searchQuery: string;
  onSearchChange: (v: string) => void;
  onNewDM: () => void;
  isLoading: boolean;
}) {
  const filtered = conversations.filter((c) =>
    c.otherUsername.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  return (
    <div className="flex flex-col h-full bg-[#2b2d31] w-60 shrink-0">
      {/* Header */}
      <div className="h-12 px-3 flex items-center border-b border-black/20 shadow-sm shrink-0">
        <div className="relative flex-1">
          <h3 className="text-sm font-semibold text-white">Messages privés</h3>
          {/* <Search className="absolute left-2 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-500 pointer-events-none" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder="Trouver une conversation"
            className="w-full bg-[#1e1f22] text-gray-300 text-sm placeholder-gray-600 rounded pl-7 pr-2 py-1 outline-none focus:ring-1 focus:ring-[#5865F2]/50 transition"
          /> */}
        </div>
      </div>

      {/* Section label + new DM */}
      {/* <div className="flex items-center justify-between px-4 pt-4 pb-1">
        <span className="text-xs font-semibold uppercase tracking-widest text-gray-500">
          Messages privés
        </span>
        <button
          onClick={onNewDM}
          title="Nouveau message"
          className="text-gray-500 hover:text-gray-200 transition-colors"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div> */}

      {/* List */}
      <div className="flex-1 overflow-y-auto py-1 space-y-0.5">
        {isLoading ? (
          <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
            <div className="w-8 h-8 border-2 border-[#5865F2] border-t-transparent rounded-full animate-spin mb-2" />
            <p className="text-xs text-gray-500">Chargement...</p>
          </div>
        ) : filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
            <MessageCircle className="w-8 h-8 text-gray-600 mb-2" />
            <p className="text-xs text-gray-500">Aucune conversation trouvée</p>
          </div>
        ) : (
          filtered.map((conv) => (
            <ConversationRow
              key={conv.id}
              conversation={conv}
              isSelected={selectedId === conv.id}
              onSelect={() => onSelect(conv.id)}
              onClose={(e) => {
                e.stopPropagation();
                onClose(conv.id);
              }}
            />
          ))
        )}
      </div>

      {/* User bar */}
      <UserBar />
    </div>
  );
}

// ─── EmptyState ───────────────────────────────────────────────────────────────

function EmptyState({ onNewDM }: { onNewDM: () => void }) {
  return (
    <div className="flex-1 flex flex-col items-center justify-center bg-[#313338] p-8">
      <div className="w-16 h-16 rounded-full bg-[#5865F2]/20 flex items-center justify-center mb-4">
        <MessageCircle className="w-8 h-8 text-[#5865F2]" />
      </div>
      <h2 className="text-xl font-bold text-white mb-2">Messages privés</h2>
      <p className="text-gray-400 text-sm text-center max-w-xs mb-6">
        Sélectionne une conversation dans la barre de gauche ou démarre-en une
        nouvelle.
      </p>
      <button
        onClick={onNewDM}
        className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-[#5865F2] hover:bg-[#4752c4] text-white text-sm font-medium transition-colors"
      >
        <Plus className="w-4 h-4" />
        Nouveau message
      </button>
    </div>
  );
}

// ─── NewDMModal ───────────────────────────────────────────────────────────────

function NewDMModal({
  isOpen,
  onClose,
  onSubmit,
}: {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (userId: number) => void;
}) {
  const [userId, setUserId] = useState("");

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = parseInt(userId, 10);
    if (!isNaN(id) && id > 0) {
      onSubmit(id);
      setUserId("");
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-[#313338] rounded-lg p-6 w-80">
        <h3 className="text-lg font-semibold text-white mb-4">
          Nouveau message
        </h3>
        <form onSubmit={handleSubmit}>
          <label className="block text-sm text-gray-400 mb-2">
            ID de l'utilisateur
          </label>
          <input
            type="number"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            placeholder="Entrez l'ID..."
            className="w-full bg-[#1e1f22] text-white rounded px-3 py-2 mb-4 outline-none focus:ring-1 focus:ring-[#5865F2]"
            autoFocus
          />
          <div className="flex gap-2 justify-end">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm text-gray-400 hover:text-white transition-colors"
            >
              Annuler
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-[#5865F2] hover:bg-[#4752c4] text-white text-sm rounded transition-colors"
            >
              Démarrer
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ─── PrivateChatLayout (composant principal) ───────────────────────────────────

export function PrivateChatLayout() {
  const [selectedConversationId, setSelectedConversationId] = useState<
    number | null
  >(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false);
  const [showSidebar, setShowSidebar] = useState(true);
  const [isNewDMModalOpen, setIsNewDMModalOpen] = useState(false);
  const { user, isAuthenticated, loading: authLoading } = useAuth();

  // Récupère l'ID de l'utilisateur courant
  const currentUserId = user?.id ?? null;

  // Hooks React Query - désactivé tant que l'utilisateur n'est pas authentifié
  const { data: conversationsDTO = [], isLoading } = useConversations(
    isAuthenticated && !authLoading,
  );
  const deleteConversationMutation = useDeleteConversation();
  const createConversationMutation = useCreateConversation();

  // Transforme les DTOs en format utilisé par le composant
  const conversations = conversationsDTO.map((dto) =>
    mapConversationDTOToPreview(dto, currentUserId),
  );

  useServerSocket();

  const selectedConversation =
    conversations.find((c) => c.id === selectedConversationId) ?? null;

  const handleSelectConversation = (id: number) => {
    setSelectedConversationId(id);
    setShowSidebar(false);
  };

  const handleCloseConversation = async (id: number) => {
    if (selectedConversationId === id) {
      setSelectedConversationId(null);
    }
    try {
      await deleteConversationMutation.mutateAsync(id);
    } catch (error) {
      console.error("Erreur lors de la suppression:", error);
    }
  };

  const handleBackToList = () => {
    setShowSidebar(true);
    setSelectedConversationId(null);
  };

  const handleNewDM = async (receiverId: number) => {
    try {
      const conversation =
        await createConversationMutation.mutateAsync(receiverId);
      // Transforme et sélectionne la nouvelle conversation
      const preview = mapConversationDTOToPreview(conversation, currentUserId);
      setSelectedConversationId(preview.id);
      setShowSidebar(false);
    } catch (error) {
      console.error("Erreur lors de la création de conversation:", error);
      alert("Impossible de créer la conversation. Vérifiez l'ID utilisateur.");
    }
  };

  return (
    <div className="chat-layout-root">
      {/* Zone 1 : DM rows (icônes serveurs à gauche) */}
      <DMRows />

      <div className="chat-dm-wrapper">
        {/*
         * Zone 2 : Sidebar DM — visible sur desktop,
         * conditionnelle sur mobile selon showSidebar
         */}
        <div
          className={`
            chat-dm-sidebar shrink-0 h-full flex flex-col
            ${showSidebar ? "flex" : "hidden"}
            md:flex
          `}
        >
          <DMSidebar
            conversations={conversations}
            selectedId={selectedConversationId}
            onSelect={handleSelectConversation}
            onClose={handleCloseConversation}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            onNewDM={() => setIsNewDMModalOpen(true)}
            isLoading={isLoading}
          />
        </div>

        {/* Zone 3 : Chatroom principale */}
        <div
          className={`
            flex-1 min-w-0 min-h-0 overflow-hidden
            ${!showSidebar || selectedConversation ? "flex" : "hidden"}
            md:flex
          `}
        >
          {selectedConversation ? (
            <PrivateChatRoom
              conversationId={selectedConversation.id}
              otherUserId={selectedConversation.otherUserId}
              otherUsername={selectedConversation.otherUsername}
              otherUserAvatarUrl={selectedConversation.otherUserAvatarUrl}
              onBack={handleBackToList}
            />
          ) : (
            <EmptyState onNewDM={() => setIsNewDMModalOpen(true)} />
          )}
        </div>

        {/* Zone 4 : Panneau droit repliable */}
        <RightPanel
          collapsed={rightPanelCollapsed}
          onToggle={() => setRightPanelCollapsed((c) => !c)}
        />

        {/* UserBar mobile (hors sidebar) */}
        <div className="chat-dm-mobile-userbar md:hidden">
          <UserBar />
        </div>
      </div>

      {/* Modal pour nouveau DM */}
      <NewDMModal
        isOpen={isNewDMModalOpen}
        onClose={() => setIsNewDMModalOpen(false)}
        onSubmit={handleNewDM}
      />
    </div>
  );
}
