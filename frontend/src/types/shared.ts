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
