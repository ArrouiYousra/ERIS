import { api } from "./client";
import type { ServerMember } from "../types/shared";

export type { ServerMember };

export async function getServerMembers(serverId: number) {
  const { data } = await api.get<ServerMember[]>(`/api/servers/${serverId}/members`);
  return data;
}

export interface UpdateMemberRolePayload {
  roleId: number;
}

export async function updateMemberRole(
  serverId: number,
  memberId: number,
  payload: UpdateMemberRolePayload,
) {
  const { data } = await api.put(
    `/api/servers/${serverId}/members/${memberId}`,
    payload,
  );
  return data;
}
