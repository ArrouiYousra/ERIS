import { api } from "./client";
import type { Server } from "../types/shared";

export type { Server };

export interface CreateServerPayload {
  name: string;
  ownerId?: number;
}

export interface UpdateServerPayload {
  name: string;
}

export async function createServer(payload: CreateServerPayload) {
  const { data } = await api.post<Server>("/api/servers", payload);
  return data;
}

export async function getAllServers(): Promise<Server[]> {
  const { data } = await api.get<Server[]>("/api/servers");
  return data;
}

export async function getServerById(id: number): Promise<Server> {
  const { data } = await api.get<Server>(`/api/servers/${id}`);
  return data;
}

export async function updateServer(id: number, payload: UpdateServerPayload) {
  const { data } = await api.put(`/api/servers/${id}`, payload);
  return data;
}

export async function deleteServer(id: number) {
  const { data } = await api.delete(`/api/servers/${id}`);
  return data;
}

export async function leaveServer(serverId: number) {
  const { data } = await api.delete(`/api/servers/${serverId}/leave`);
  return data;
}