import { useEffect, useState, useCallback, useRef } from 'react';
import { useSocket } from '../api/wsApi';
import { useAuth } from './useAuth';

const TYPING_TIMEOUT = 2000;

export function useTyping(channelId: number | null) {
  const { subscribe, publish, connected } = useSocket();
  const { user } = useAuth();
  const [typingUsers, setTypingUsers] = useState<Map<number, string>>(new Map());
  const timerRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
  const isTypingRef = useRef<boolean>(false);

  // Subscribe aux events typing du channel
  useEffect(() => {
    if (!channelId || !connected) return;

    const sub = subscribe(`/topic/channels/${channelId}/typing`, (msg) => {
      const data = JSON.parse(msg.body);
      // Ignorer ses propres events
      if (data.userId === user?.id) return;

      setTypingUsers((prev) => {
        const next = new Map(prev);
        if (data.typing) {
          next.set(data.userId, data.username || 'Quelqu\'un');
        } else {
          next.delete(data.userId);
        }
        return next;
      });
    });

    return () => {
      sub?.unsubscribe();
      setTypingUsers(new Map());
    };
  }, [channelId, connected, subscribe, user?.id]);

  // Auto-clean les typing users après timeout (safety net)
  useEffect(() => {
    if (typingUsers.size === 0) return;
    const timer = setTimeout(() => setTypingUsers(new Map()), 5000);
    return () => clearTimeout(timer);
  }, [typingUsers]);

  // Envoyer son propre typing status
  const sendTyping = useCallback(
    (isTyping: boolean) => {
      if (!channelId || !connected || !user?.id) return;
      publish('/app/typing', {
        userId: user.id,
        username: user.displayName || user.username,
        channelId,
        typing: isTyping,
      });
    },
    [channelId, connected, user, publish],
  );

  // Appelé par l'input onChange
  const onInputChange = useCallback(() => {
    if (!isTypingRef.current) {
      isTypingRef.current = true;
      sendTyping(true);
    }
    clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => {
      isTypingRef.current = false;
      sendTyping(false);
    }, TYPING_TIMEOUT);
  }, [sendTyping]);

  // Stop typing (appelé au submit)
  const stopTyping = useCallback(() => {
    clearTimeout(timerRef.current);
    if (isTypingRef.current) {
      isTypingRef.current = false;
      sendTyping(false);
    }
  }, [sendTyping]);

  // Texte à afficher
  const typingText = (() => {
    const names = Array.from(typingUsers.values());
    if (names.length === 0) return null;
    if (names.length === 1) return `${names[0]} est en train d'écrire...`;
    if (names.length === 2) return `${names[0]} et ${names[1]} écrivent...`;
    return `${names.length} personnes écrivent...`;
  })();

  return { typingText, onInputChange, stopTyping };
}