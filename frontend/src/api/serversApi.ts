import { api } from "./client";

export interface Server {
  id: number;
  name: string;
  ownerId: number;
}

export interface CreateServerPayload {
  name: string;
  ownerId?: number;
}

export interface UpdateServerPayload {
  name: string;
}

export async function createServer(payload: CreateServerPayload) {
  const { data } = await api.post("/api/servers", payload);
  return data;
}

export async function getAllServers() {
  const { data } = await api.get("/api/servers");
  return data;
}

export async function getServerById(id: number) {
  const { data } = await api.get(`/api/servers/${id}`);
  return data;
}

export async function updateServer(id: number, payload: UpdateServerPayload) {
  const { data } = await api.put(`/api/servers/${id}`, payload);
  return data;
}

export async function joinServerByInviteLink(inviteLinkOrCode: string) {
  const { data } = await api.post("/api/servers/join", {
    inviteLink: inviteLinkOrCode.trim(),
  });
  return data;
}
