import { api } from "./client";
import type { ServerMember } from "../types/shared";

export type { ServerMember };

export interface UpdateMemberPayload {
  roleId: number;
}

export async function getServerMembers(serverId: number) {
  const { data } = await api.get<ServerMember[]>(`/api/servers/${serverId}/members`);
  return data;
}

export async function updateMemberRole(serverId: number, memberId: number, roleId: UpdateMemberPayload) {
  const { data } = await api.put<ServerMember[]>(`/api/servers/${serverId}/members/${memberId}`, roleId);
  return data;
}

export async function getServerRoles(serverId: number) {
  const { data } = await api.get<ServerRole[]>(`/api/servers/${serverId}/roles`);
  return data;
}
