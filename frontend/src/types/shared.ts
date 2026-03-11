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
