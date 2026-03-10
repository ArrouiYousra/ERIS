import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getChannelsByServer,
  createChannel,
  updateChannel,
  deleteChannel,
  type CreateChannelPayload,
  type UpdateChannelPayload,
} from "../api/channelsApi";

export function useChannels(serverId: number | null) {
  return useQuery({
    queryKey: ["channels", serverId],
    queryFn: () => (serverId ? getChannelsByServer(serverId) : []),
    enabled: !!serverId,
  });
}

export function useCreateChannel() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      serverId,
      payload,
    }: {
      serverId: number;
      payload: CreateChannelPayload;
    }) => createChannel(serverId, payload),
    onSuccess: (_, variables) => {
      // Force refetch of channels list
      queryClient.invalidateQueries({
        queryKey: ["channels", variables.serverId],
      });
      queryClient.refetchQueries({
        queryKey: ["channels", variables.serverId],
      });
    },
  });
}

export function useUpdateChannel() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      channelId,
      payload,
    }: {
      channelId: number;
      serverId: number;
      payload: UpdateChannelPayload;
    }) => updateChannel(channelId, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["channels", variables.serverId],
      });
      queryClient.refetchQueries({
        queryKey: ["channels", variables.serverId],
      });
    },
  });
}

export function useDeleteChannel() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ channelId }: { channelId: number; serverId: number }) =>
      deleteChannel(channelId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["channels", variables.serverId],
      });
      queryClient.refetchQueries({
        queryKey: ["channels", variables.serverId],
      });
    },
  });
}
