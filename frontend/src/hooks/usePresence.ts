import { useEffect, useState } from 'react';
import { useSocket } from '../api/wsApi';

export function usePresence(serverId: number | null) {
  const { subscribe, connected } = useSocket();
  const [onlineUserIds, setOnlineUserIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (!serverId || !connected) return;

    const sub = subscribe(`/topic/servers/${serverId}/presence`, (msg) => {
      const ids: number[] = JSON.parse(msg.body);
      setOnlineUserIds(new Set(ids));
    });

    return () => {
      sub?.unsubscribe();
      setOnlineUserIds(new Set());
    };
  }, [serverId, connected, subscribe]);

  return { onlineUserIds };
}