import { useEffect} from 'react';
import { useSocket } from '../api/wsApi';
import { useQueryClient } from '@tanstack/react-query';


export function useServerSocket() {
  const { subscribe, connected} = useSocket();
  const queryClient = useQueryClient();

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!connected) return;
    const sub = subscribe(`/topic/servers`, () => {
        queryClient.invalidateQueries({ queryKey: ["servers"] });
    });
    const sub_reload = subscribe(`/topic/channels`, () => {
        queryClient.invalidateQueries({ queryKey: ["channels"] });
    });
    return () => {      
        sub?.unsubscribe();
        sub_reload?.unsubscribe();
    };
  }, [connected, subscribe, queryClient]);
}