import React, { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../hooks/useAuth';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8081/ws';

interface SocketContextValue {
  connected: boolean;
  subscribe: (dest: string, cb: (msg: IMessage) => void) => StompSubscription | null;
  publish: (dest: string, body: object) => void;
}

const SocketContext = createContext<SocketContextValue>({
  connected: false,
  subscribe: () => null,
  publish: () => {},
});

export const useSocket = () => useContext(SocketContext);

export function SocketProvider({ children }: { children: React.ReactNode }) {
  const { user, isAuthenticated } = useAuth();
  const clientRef = useRef<Client | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token || !isAuthenticated) return;

    const stomp = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      debug: (str) => console.log('[STOMP]', str),
      onConnect: () => {
        console.log('✅ STOMP connected');
        setConnected(true);

        // Enregistrer la présence si on a le user.id 
        if (user?.id) {
          stomp.publish({
            destination: '/app/presence.connect',
            body: JSON.stringify({ userId: user.id }),
          });
        }
      },
      onDisconnect: () => {
        console.log('❌ STOMP disconnected');
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message']);
      },
    });

    stomp.activate();
    clientRef.current = stomp;

    return () => {
      stomp.deactivate();
      clientRef.current = null;
      setConnected(false);
    };
  }, [isAuthenticated, user?.id]);

  const subscribe = useCallback(
    (dest: string, cb: (msg: IMessage) => void) => {
      if (!clientRef.current?.connected) return null;
      return clientRef.current.subscribe(dest, cb);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [connected],
  );

  const publish = useCallback(
    (dest: string, body: object) => {
      if (!clientRef.current?.connected) return;
      clientRef.current.publish({ destination: dest, body: JSON.stringify(body) });
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [connected],
  );

  return (
    <SocketContext.Provider value={{ connected, subscribe, publish }}>
      {children}
    </SocketContext.Provider>
  );
}