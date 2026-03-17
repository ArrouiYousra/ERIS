import { useEffect } from 'react';
import { useSocket } from '../api/wsApi';
import { useQueryClient } from '@tanstack/react-query';


export function usePresenceSocket(serverId: number | null) {
  const { subscribe, connected } = useSocket();
  const queryClient = useQueryClient();

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!connected || serverId === null) return;
    const sub = subscribe(`/topic/server_member/${serverId}`, () => {
      queryClient.invalidateQueries({ queryKey: ["serverMembers", serverId] });
      queryClient.invalidateQueries({ queryKey: ["servers"] });
    });
    return () => {
      sub?.unsubscribe();
    };
  }, [connected, subscribe, queryClient, serverId]);
}