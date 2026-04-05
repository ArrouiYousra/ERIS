export interface AuthUser {
  id: number;
  email?: string;
  username?: string;
  displayName?: string;
}

export interface ChatMessage {
  id: number;
  senderId: number;
  senderUsername?: string;
  content: string;
  createdAt: string;
  channelId?: number;
}

// ─── Private Messages / Conversations ───────────────────────────────────────

export interface ParticipantDTO {
  userId: number;
  username: string;
}

export interface LastPrivateMessageDTO {
  messageId: number;
  senderId: number;
  content: string;
  createdAt: string;
}

export interface ConversationPreviewDTO {
  conversationId: number;
  participants: ParticipantDTO[];
  lastPrivateMessage: LastPrivateMessageDTO | null;
}

export interface PrivateMessageDTO {
  messageId: number;
  conversationId: number;
  sender: {
    userId: number;
    username: string;
  };
  content: string;
  createdAt: string;
  updatedAt: string | null;
}

export type PrivateMessageEvent =
  | { type: "NEW"; data: PrivateMessageDTO }
  | { type: "EDIT"; data: PrivateMessageDTO }
  | { type: "DELETE"; data: number };

export interface ConversationDetailsDTO {
  conversationId: number;
  participants: ParticipantDTO[];
  privateMessages: PrivateMessageDTO[];
}

// Legacy types (pour compatibilité si utilisés ailleurs)
export interface PrivateMessage {
  senderId: number;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface Conversation {
  id: number;
  userId: number;
  privateMessage: PrivateMessage;
}

export interface Server {
  id: number;
  name: string;
  ownerId: number;
}

export interface ServerMember {
  id: number;
  username: string;
  serverId: number;
  roleName: string;
  nickname: string;
  userId: number;
  typingStatus: boolean;
  joinedAt: string;
}

export interface ServerRole {
  id: number;
  name: string;
}
