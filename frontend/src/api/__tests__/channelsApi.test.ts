import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getChannelsByServer, createChannel, updateChannel, deleteChannel } from '../channelsApi';
import { api } from '../client';

vi.mock('../client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: { request: { use: vi.fn() } },
  },
}));

describe('channelsApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getChannelsByServer', () => {
    it('calls GET /api/servers/:serverId/channels', async () => {
      const channels = [{ id: 1, name: 'general', serverId: 1 }];
      (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: channels });

      const result = await getChannelsByServer(1);

      expect(api.get).toHaveBeenCalledWith('/api/servers/1/channels');
      expect(result).toEqual(channels);
    });
  });

  describe('createChannel', () => {
    it('calls POST /api/servers/:serverId/channels', async () => {
      const created = { id: 2, name: 'new-channel', serverId: 1 };
      (api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: created });

      const result = await createChannel(1, { name: 'new-channel', serverId: 1 });

      expect(api.post).toHaveBeenCalledWith('/api/servers/1/channels', {
        name: 'new-channel',
        serverId: 1,
      });
      expect(result).toEqual(created);
    });
  });

  describe('updateChannel', () => {
    it('calls PUT /api/channels/:channelId', async () => {
      const updated = { id: 1, name: 'renamed' };
      (api.put as ReturnType<typeof vi.fn>).mockResolvedValue({ data: updated });

      const result = await updateChannel(1, { name: 'renamed' });

      expect(api.put).toHaveBeenCalledWith('/api/channels/1', { name: 'renamed' });
      expect(result).toEqual(updated);
    });
  });

  describe('deleteChannel', () => {
    it('calls DELETE /api/channels/:channelId', async () => {
      (api.delete as ReturnType<typeof vi.fn>).mockResolvedValue({ data: 'deleted' });

      const result = await deleteChannel(1);

      expect(api.delete).toHaveBeenCalledWith('/api/channels/1');
      expect(result).toBe('deleted');
    });
  });
});
