import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getAllServers,
  getServerById,
  createServer,
  updateServer,
  deleteServer as deleteServerApi,
  leaveServer,
  type CreateServerPayload,
  type UpdateServerPayload,
} from "../api/serversApi";
import {
  getServerMembers,
  updateMemberRole,
  getServerRoles,
  type UpdateMemberPayload,
} from "../api/serverMembersApi";

import { useAuth } from "./useAuth";

export function useServers() {
  const { isAuthenticated } = useAuth();
  return useQuery({
    queryKey: ["servers"],
    queryFn: getAllServers,
    enabled: isAuthenticated,
  });
}

export function useServer(id: number | null) {
  return useQuery({
    queryKey: ["servers", id],
    queryFn: () => (id ? getServerById(id) : null),
    enabled: !!id,
  });
}

export function useCreateServer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateServerPayload) => createServer(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    },
  });
}

export function useUpdateServer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: UpdateServerPayload;
    }) => updateServer(id, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["servers"] });
      queryClient.invalidateQueries({ queryKey: ["servers", variables.id] });
    },
  });
}

export function useDeleteServer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteServerApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    },
  });
}

export function useLeaveServer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => leaveServer(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    },
  });
}

export function useServerMembers(serverId: number | null) {
  return useQuery({
    queryKey: ["serverMembers", serverId],
    queryFn: () => (serverId ? getServerMembers(serverId) : []),
    enabled: !!serverId,
  });
}

export function useUpdateMemberRole() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      serverId,
      memberId,
      payload,
    }: {
      serverId: number;
      memberId: number;
      payload: UpdateMemberPayload;
    }) => updateMemberRole(serverId, memberId, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["serverMembers", variables.serverId] });
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    },
  });
}

export function useServerRoles(serverId: number | null) {
  return useQuery({
    queryKey: ["serverRoles", serverId],
    queryFn: () => (serverId ? getServerRoles(serverId) : []),
    enabled: !!serverId,
  });
}
