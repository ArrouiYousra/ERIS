import { api } from "./client";

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

export async function getServerMembers(serverId: number) {
  const { data } = await api.get<ServerMember[]>(`/api/servers/${serverId}/members`);
  return data;
}
