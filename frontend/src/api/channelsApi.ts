import { api } from "./client";

export interface Channel {
  id: number;
  name: string;
  topic?: string;
  isPrivate?: boolean;
  serverId: number;
  createdAt?: string;
}

export interface CreateChannelPayload {
  name: string;
  topic?: string;
  isPrivate?: boolean;
  serverId: number;
}

export interface UpdateChannelPayload {
  name?: string;
  topic?: string;
  isPrivate?: boolean;
}

export async function getChannelsByServer(serverId: number) {
  const { data } = await api.get<Channel[]>(`/api/servers/${serverId}/channels`);
  return data;
}

export async function createChannel(
  serverId: number,
  payload: CreateChannelPayload,
) {
  const { data } = await api.post<Channel>(`/api/servers/${serverId}/channels`, payload);
  return data;
}

export async function updateChannel(
  channelId: number,
  payload: UpdateChannelPayload,
) {
  const { data } = await api.put<Channel>(`/api/channels/${channelId}`, payload);
  return data;
}

export async function deleteChannel(channelId: number) {
  const { data } = await api.delete(`/api/channels/${channelId}`);
  return data;
}
