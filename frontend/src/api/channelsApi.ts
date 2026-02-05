import { api } from "./client";

export interface Channel {
  id: number;
  name: string;
  serverId: number;
  createdAt?: string;
}

export interface CreateChannelPayload {
  name: string;
  serverId: number;
}

export async function getChannelsByServer(serverId: number) {
  const { data } = await api.get(`/api/servers/${serverId}/channels`);
  return data;
}

export async function createChannel(
  serverId: number,
  payload: CreateChannelPayload,
) {
  const { data } = await api.post(`/api/servers/${serverId}/channels`, payload);
  return data;
}
