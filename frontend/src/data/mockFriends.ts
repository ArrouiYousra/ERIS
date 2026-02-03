import type { Friend, DM } from "../types/friends";

export const MOCK_FRIENDS: Friend[] = [
  { id: "1", displayName: "Alice", username: "alice", status: "online", lastMessagePreview: "Salut !", avatarColor: "#5865F2" },
  { id: "2", displayName: "Bob", username: "bob", status: "online", lastMessagePreview: "À plus", avatarColor: "#57F287" },
  { id: "3", displayName: "Charlie", username: "charlie", status: "online", lastMessagePreview: "Ok", avatarColor: "#FEE75C" },
  { id: "4", displayName: "Diana", username: "diana", status: "offline", lastMessagePreview: "Bye", avatarColor: "#EB459E" },
  { id: "5", displayName: "Eve", username: "eve", status: "offline", lastMessagePreview: "...", avatarColor: "#ED4245" },
  { id: "6", displayName: "Frank", username: "frank", status: "offline", lastMessagePreview: "Ciao", avatarColor: "#5865F2" },
  { id: "7", displayName: "Grace", username: "grace", status: "idle", lastMessagePreview: "À demain", avatarColor: "#57F287" },
  { id: "8", displayName: "Henry", username: "henry", status: "idle", lastMessagePreview: "👍", avatarColor: "#FEE75C" },
];

export const MOCK_DMS: DM[] = [
  { id: "dm1", friend: MOCK_FRIENDS[0], lastMessagePreview: "Salut !", unread: true },
  { id: "dm2", friend: MOCK_FRIENDS[1], lastMessagePreview: "À plus", unread: false },
  { id: "dm3", friend: MOCK_FRIENDS[2], lastMessagePreview: "Ok", unread: false },
  { id: "dm4", friend: MOCK_FRIENDS[3], lastMessagePreview: "Bye", unread: false },
  { id: "dm5", friend: MOCK_FRIENDS[6], lastMessagePreview: "À demain", unread: false },
];
