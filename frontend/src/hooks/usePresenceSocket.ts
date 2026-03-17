import { useEffect} from 'react';
import { useSocket } from '../api/wsApi';
import { useQueryClient } from '@tanstack/react-query';


export function useServerSocket() {
  const { subscribe, connected} = useSocket();
  const queryClient = useQueryClient();

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!connected) return;
    const sub = subscribe(`/topic/server_member`, () => {
        queryClient.invalidateQueries({ queryKey: ["servers"] });
    });
    return () => {      
        sub?.unsubscribe();
    };
  }, [connected, subscribe, queryClient]);
}