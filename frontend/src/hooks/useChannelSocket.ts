import { useEffect, useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useSocket } from '../api/wsApi';
import { useAuth } from './useAuth';
import type { Message } from './useMessages';

export function useChannelSocket(channelId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const queryClient = useQueryClient();
  const { user } = useAuth();

  // Subscribe aux messages du channel
  useEffect(() => {
    if (!channelId || !connected) return;

    const sub = subscribe(`/topic/channels/${channelId}`, (msg) => {
      const newMessage = JSON.parse(msg.body);
      queryClient.setQueryData(['messages', channelId], (old: Message[] = []) => {  //eslint
        const existingMessage = (old.some((m) => m.id === newMessage.id));
        if (existingMessage) {
          return old.map((m) => m.id === newMessage.id ? newMessage : m);
        }
        return [...old, newMessage];
      });
    });

    return () => {
      sub?.unsubscribe();
    };
  }, [channelId, connected, subscribe, queryClient]);

  // Envoyer un message
  const sendMessage = useCallback(
    (content: string) => {
      if (!channelId || !connected || !user?.id) {
        console.warn('sendMessage bloqué:', { channelId, connected, userId: user?.id });
        return;
      }
      publish('/app/chat', { senderId: user.id, channelId, content });
    },
    [channelId, connected, user?.id, publish],
  );

  return { sendMessage, connected };
}