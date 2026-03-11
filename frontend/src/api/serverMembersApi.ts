import { api } from "./client";
import type { ServerMember } from "../types/shared";

export type { ServerMember };

export async function getServerMembers(serverId: number) {
  const { data } = await api.get<ServerMember[]>(`/api/servers/${serverId}/members`);
  return data;
}
