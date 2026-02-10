import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getAllServers,
  getServerById,
  createServer,
  updateServer,
  deleteServer as deleteServerApi,
  leaveServer as leaveServerApi,
  type Server,
  type CreateServerPayload,
  type UpdateServerPayload,
} from "../api/serversApi";
import { getServerMembers } from "../api/serverMembersApi";

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

export function useServerMembers(serverId: number | null) {
  return useQuery({
    queryKey: ["serverMembers", serverId],
    queryFn: () => (serverId ? getServerMembers(serverId) : []),
    enabled: !!serverId,
  });
}