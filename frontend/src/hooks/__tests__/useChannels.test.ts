import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { createElement } from 'react';
import { useChannels } from '../useChannels';

vi.mock('../../api/channelsApi', () => ({
  getChannelsByServer: vi.fn(),
  createChannel: vi.fn(),
  updateChannel: vi.fn(),
  deleteChannel: vi.fn(),
}));

import { getChannelsByServer } from '../../api/channelsApi';

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return ({ children }: { children: ReactNode }) =>
    createElement(QueryClientProvider, { client: queryClient }, children);
}

describe('useChannels', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('returns empty data when serverId is null', () => {
    const { result } = renderHook(() => useChannels(null), {
      wrapper: createWrapper(),
    });

    // Query should be disabled, so no data fetched
    expect(result.current.data).toBeUndefined();
    expect(result.current.isFetching).toBe(false);
  });

  it('fetches channels when serverId is provided', async () => {
    const channels = [
      { id: 1, name: 'general', serverId: 1 },
      { id: 2, name: 'random', serverId: 1 },
    ];
    (getChannelsByServer as ReturnType<typeof vi.fn>).mockResolvedValue(channels);

    const { result } = renderHook(() => useChannels(1), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data).toEqual(channels);
    expect(getChannelsByServer).toHaveBeenCalledWith(1);
  });

  it('handles API error', async () => {
    (getChannelsByServer as ReturnType<typeof vi.fn>).mockRejectedValue(new Error('Server error'));

    const { result } = renderHook(() => useChannels(1), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });
  });
});
