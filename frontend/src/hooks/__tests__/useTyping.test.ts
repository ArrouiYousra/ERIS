import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';

const mockSubscribe = vi.fn();
const mockPublish = vi.fn();

vi.mock('../../api/wsApi', () => ({
  useSocket: () => ({
    subscribe: mockSubscribe,
    publish: mockPublish,
    connected: true,
  }),
  SocketProvider: ({ children }: any) => children,
}));

vi.mock('../useAuth', () => ({
  useAuth: () => ({
    user: { id: 1, username: 'testuser', displayName: 'Test' },
    isAuthenticated: true,
    loading: false,
    login: vi.fn(),
    signup: vi.fn(),
    logout: vi.fn(),
  }),
}));

import { useTyping } from '../useTyping';

describe('useTyping', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.useFakeTimers();
    mockSubscribe.mockReturnValue({ unsubscribe: vi.fn() });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('returns null typingText when no one is typing', () => {
    const { result } = renderHook(() => useTyping(1));

    expect(result.current.typingText).toBeNull();
  });

  it('subscribes to typing topic when channelId is provided', () => {
    renderHook(() => useTyping(1));

    expect(mockSubscribe).toHaveBeenCalledWith(
      '/topic/channels/1/typing',
      expect.any(Function),
    );
  });

  it('does not subscribe when channelId is null', () => {
    renderHook(() => useTyping(null));

    expect(mockSubscribe).not.toHaveBeenCalled();
  });

  it('sendTyping publishes via websocket on onInputChange', () => {
    const { result } = renderHook(() => useTyping(1));

    act(() => {
      result.current.onInputChange();
    });

    expect(mockPublish).toHaveBeenCalledWith('/app/typing', {
      userId: 1,
      username: 'Test',
      channelId: 1,
      typing: true,
    });
  });

  it('stopTyping sends typing=false after being active', () => {
    const { result } = renderHook(() => useTyping(1));

    act(() => {
      result.current.onInputChange(); // start typing
    });
    mockPublish.mockClear();

    act(() => {
      result.current.stopTyping();
    });

    expect(mockPublish).toHaveBeenCalledWith('/app/typing', {
      userId: 1,
      username: 'Test',
      channelId: 1,
      typing: false,
    });
  });

  it('onInputChange auto-stops typing after timeout', () => {
    const { result } = renderHook(() => useTyping(1));

    act(() => {
      result.current.onInputChange();
    });
    mockPublish.mockClear();

    // Advance past TYPING_TIMEOUT (2000ms)
    act(() => {
      vi.advanceTimersByTime(2100);
    });

    expect(mockPublish).toHaveBeenCalledWith('/app/typing', {
      userId: 1,
      username: 'Test',
      channelId: 1,
      typing: false,
    });
  });

  it('shows typing text for 1 user', () => {
    // Simulate receiving a typing event from another user
    let subscribeCallback: (msg: any) => void;
    mockSubscribe.mockImplementation((_topic: string, cb: any) => {
      subscribeCallback = cb;
      return { unsubscribe: vi.fn() };
    });

    const { result } = renderHook(() => useTyping(1));

    act(() => {
      subscribeCallback!({
        body: JSON.stringify({ userId: 2, username: 'Alice', typing: true }),
      });
    });

    expect(result.current.typingText).toBe("Alice est en train d'écrire...");
  });

  it('shows typing text for 2 users', () => {
    let subscribeCallback: (msg: any) => void;
    mockSubscribe.mockImplementation((_topic: string, cb: any) => {
      subscribeCallback = cb;
      return { unsubscribe: vi.fn() };
    });

    const { result } = renderHook(() => useTyping(1));

    act(() => {
      subscribeCallback!({
        body: JSON.stringify({ userId: 2, username: 'Alice', typing: true }),
      });
      subscribeCallback!({
        body: JSON.stringify({ userId: 3, username: 'Bob', typing: true }),
      });
    });

    expect(result.current.typingText).toBe('Alice et Bob écrivent...');
  });

  it('shows typing text for 3+ users', () => {
    let subscribeCallback: (msg: any) => void;
    mockSubscribe.mockImplementation((_topic: string, cb: any) => {
      subscribeCallback = cb;
      return { unsubscribe: vi.fn() };
    });

    const { result } = renderHook(() => useTyping(1));

    act(() => {
      subscribeCallback!({
        body: JSON.stringify({ userId: 2, username: 'Alice', typing: true }),
      });
      subscribeCallback!({
        body: JSON.stringify({ userId: 3, username: 'Bob', typing: true }),
      });
      subscribeCallback!({
        body: JSON.stringify({ userId: 4, username: 'Charlie', typing: true }),
      });
    });

    expect(result.current.typingText).toBe('3 personnes écrivent...');
  });

  it('ignores own typing events', () => {
    let subscribeCallback: (msg: any) => void;
    mockSubscribe.mockImplementation((_topic: string, cb: any) => {
      subscribeCallback = cb;
      return { unsubscribe: vi.fn() };
    });

    const { result } = renderHook(() => useTyping(1));

    act(() => {
      // userId 1 is our own user
      subscribeCallback!({
        body: JSON.stringify({ userId: 1, username: 'testuser', typing: true }),
      });
    });

    expect(result.current.typingText).toBeNull();
  });

  it('removes user from typing when they stop', () => {
    let subscribeCallback: (msg: any) => void;
    mockSubscribe.mockImplementation((_topic: string, cb: any) => {
      subscribeCallback = cb;
      return { unsubscribe: vi.fn() };
    });

    const { result } = renderHook(() => useTyping(1));

    act(() => {
      subscribeCallback!({
        body: JSON.stringify({ userId: 2, username: 'Alice', typing: true }),
      });
    });
    expect(result.current.typingText).toBe("Alice est en train d'écrire...");

    act(() => {
      subscribeCallback!({
        body: JSON.stringify({ userId: 2, username: 'Alice', typing: false }),
      });
    });
    expect(result.current.typingText).toBeNull();
  });
});
