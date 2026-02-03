export type FriendStatus = "online" | "offline" | "idle";

export interface Friend {
  id: string;
  displayName: string;
  username: string;
  status: FriendStatus;
  lastMessagePreview?: string;
  avatarColor: string;
}

export interface DM {
  id: string;
  friend: Friend;
  lastMessagePreview: string;
  unread?: boolean;
}

export type MainContentTab = "ADD" | "ONLINE" | "ALL" | "PENDING" | "BLOCKED";
