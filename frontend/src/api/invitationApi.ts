import { api } from "./client";

// src/types/invite.ts
export interface JoinInviteRequest {
  code: string;
}

export interface JoinInviteResponse {
  serverId: number;
  serverName: string;
  message: string;
}

export interface InvitationDTO {
  code: string;
  expiresAt?: string;
}

export async function createInvitation(serverId: number): Promise<InvitationDTO> {
  const { data } = await api.post<InvitationDTO>(
    `/api/servers/${serverId}/invite`,  // ← ajoute le / au début
  );
  return data;
}

export async function joinWithInvitation(code: string) {
  const { data } = await api.post<JoinInviteResponse>(`/api/servers/join`, {  // ← ajoute le /
    code,
  });
  return data;
}
