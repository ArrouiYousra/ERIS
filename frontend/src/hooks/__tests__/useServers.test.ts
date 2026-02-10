import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { createElement } from 'react';
import { useServer } from '../useServers';

vi.mock('../../api/serversApi', () => ({
  getAllServers: vi.fn(),
  getServerById: vi.fn(),
  createServer: vi.fn(),
  updateServer: vi.fn(),
}));

vi.mock('../../api/serverMembersApi', () => ({
  getServerMember: vi.fn(),
}));

vi.mock('../useAuth', () => ({
  useAuth: () => ({
    isAuthenticated: true,
    user: { id: 1, email: 'test@example.com' },
    loading: false,
    login: vi.fn(),
    signup: vi.fn(),
    logout: vi.fn(),
  }),
}));

import { getServerById } from '../../api/serversApi';

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return ({ children }: { children: ReactNode }) =>
    createElement(QueryClientProvider, { client: queryClient }, children);
}

describe('useServer', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('returns undefined data when id is null', () => {
    const { result } = renderHook(() => useServer(null), {
      wrapper: createWrapper(),
    });

    expect(result.current.data).toBeUndefined();
    expect(result.current.isFetching).toBe(false);
  });

  it('fetches server when id is provided', async () => {
    const server = { id: 1, name: 'Test Server', ownerId: 1 };
    (getServerById as ReturnType<typeof vi.fn>).mockResolvedValue(server);

    const { result } = renderHook(() => useServer(1), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data).toEqual(server);
    expect(getServerById).toHaveBeenCalledWith(1);
  });

  it('handles API error', async () => {
    (getServerById as ReturnType<typeof vi.fn>).mockRejectedValue(new Error('Not found'));

    const { result } = renderHook(() => useServer(1), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });
  });
});
