import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getChannelsByServer,
  createChannel,
  type CreateChannelPayload,
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
      queryClient.invalidateQueries({
        queryKey: ["servers", variables.serverId],
      });
      queryClient.invalidateQueries({
        queryKey: ["channels", variables.serverId],
      });
    },
  });
}
