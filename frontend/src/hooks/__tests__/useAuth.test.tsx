import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act, waitFor } from '@testing-library/react';
import type { ReactNode } from 'react';
import { AuthProvider, useAuth } from '../useAuth';

// Mock the API modules
vi.mock('../../api/authApi', () => ({
  login: vi.fn(),
  signup: vi.fn(),
}));

vi.mock('../../api/client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    interceptors: { request: { use: vi.fn() } },
  },
}));

import { login as loginApi, signup as signupApi } from '../../api/authApi';
import { api } from '../../api/client';

const wrapper = ({ children }: { children: ReactNode }) => (
  <AuthProvider>{children}</AuthProvider>
);

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Mock localStorage
    vi.stubGlobal('localStorage', {
      getItem: vi.fn().mockReturnValue(null),
      setItem: vi.fn(),
      removeItem: vi.fn(),
      clear: vi.fn(),
    });
  });

  it('throws if used outside AuthProvider', () => {
    // Suppress console.error for expected error
    const spy = vi.spyOn(console, 'error').mockImplementation(() => {});
    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');
    spy.mockRestore();
  });

  it('starts with loading=true when no token', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);

    const { result } = renderHook(() => useAuth(), { wrapper });

    // After mount, loading should become false since there's no token
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
    expect(result.current.user).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it('fetches user on mount when token exists', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('existing-token');
    (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 1, email: 'test@example.com' },
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
    expect(result.current.user).toEqual({ id: 1, email: 'test@example.com' });
    expect(result.current.isAuthenticated).toBe(true);
  });

  it('clears token when /me fails on mount', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('bad-token');
    (api.get as ReturnType<typeof vi.fn>).mockRejectedValue(new Error('401'));

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
    expect(localStorage.removeItem).toHaveBeenCalledWith('access_token');
    expect(result.current.user).toBeNull();
  });

  it('login stores token and fetches user', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);
    (loginApi as ReturnType<typeof vi.fn>).mockResolvedValue({ token: 'new-token' });
    (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 1, email: 'a@b.c' },
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => expect(result.current.loading).toBe(false));

    await act(async () => {
      await result.current.login('a@b.c', 'pass');
    });

    expect(localStorage.setItem).toHaveBeenCalledWith('access_token', 'new-token');
    expect(result.current.isAuthenticated).toBe(true);
  });

  it('logout clears user and token', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('token');
    (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 1, email: 'test@example.com' },
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => expect(result.current.isAuthenticated).toBe(true));

    act(() => {
      result.current.logout();
    });

    expect(localStorage.removeItem).toHaveBeenCalledWith('access_token');
    expect(result.current.user).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it('signup calls signupApi then auto-login', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);
    (signupApi as ReturnType<typeof vi.fn>).mockResolvedValue({ id: 1 });
    (loginApi as ReturnType<typeof vi.fn>).mockResolvedValue({ token: 'new-token' });
    (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 1, email: 'new@b.c' },
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => expect(result.current.loading).toBe(false));

    await act(async () => {
      await result.current.signup('new@b.c', 'user', 'Pass1234', 'User', '2000-01-01');
    });

    expect(signupApi).toHaveBeenCalled();
    expect(loginApi).toHaveBeenCalled();
    expect(result.current.isAuthenticated).toBe(true);
  });
});
