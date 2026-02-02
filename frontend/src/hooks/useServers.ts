import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getAllServers,
  getServerById,
  createServer,
  updateServer,
  type Server,
  type CreateServerPayload,
  type UpdateServerPayload,
} from "../api/serversApi";

export function useServers() {
  return useQuery({
    queryKey: ["servers"],
    queryFn: getAllServers,
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
    mutationFn: ({ id, payload }: { id: number; payload: UpdateServerPayload }) =>
      updateServer(id, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["servers"] });
      queryClient.invalidateQueries({ queryKey: ["servers", variables.id] });
    },
  });
}
