import { api } from "./client";

export interface ServerMember {
  id: number;
  serverId: number;
  roleId: number;
  userId: number;
  nickname: string;
  typingStatus: boolean;
  joinedAt: string;
}

export async function getServerMember(serverId: number) {
  const { data } = await api.get(`servers/${serverId}/members`);
  return data;
}
