import { api } from "./client";

export interface Channel {
  id: number;
  name: string;
  serverId?: number;
  createdAt?: string;
}

export interface CreateChannelPayload {
  name: string;
  serverId: number;
}

export async function getChannelsByServer(serverId: number) {
  // Note: Le backend n'a pas encore d'endpoint GET pour les channels d'un server
  // Pour l'instant, on retourne un tableau vide
  // TODO: Implémenter GET /api/servers/{serverId}/channels dans le backend
  return [];
}

export async function createChannel(serverId: number, payload: CreateChannelPayload) {
  const { data } = await api.post(`/api/servers/${serverId}/channels`, payload);
  return data;
}
