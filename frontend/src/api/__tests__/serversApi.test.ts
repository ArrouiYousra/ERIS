import { describe, it, expect, vi, beforeEach } from 'vitest';
import { createServer, getAllServers, getServerById, updateServer } from '../serversApi';
import { api } from '../client';

vi.mock('../client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    interceptors: { request: { use: vi.fn() } },
  },
}));

describe('serversApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('createServer', () => {
    it('calls POST /api/servers', async () => {
      const created = { id: 1, name: 'My Server' };
      (api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: created });

      const result = await createServer({ name: 'My Server' });

      expect(api.post).toHaveBeenCalledWith('/api/servers', { name: 'My Server' });
      expect(result).toEqual(created);
    });
  });

  describe('getAllServers', () => {
    it('calls GET /api/servers', async () => {
      const servers = [{ id: 1, name: 'Server1' }];
      (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: servers });

      const result = await getAllServers();

      expect(api.get).toHaveBeenCalledWith('/api/servers');
      expect(result).toEqual(servers);
    });
  });

  describe('getServerById', () => {
    it('calls GET /api/servers/:id', async () => {
      const server = { id: 1, name: 'Server1' };
      (api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: server });

      const result = await getServerById(1);

      expect(api.get).toHaveBeenCalledWith('/api/servers/1');
      expect(result).toEqual(server);
    });
  });

  describe('updateServer', () => {
    it('calls PUT /api/servers/:id', async () => {
      const updated = { id: 1, name: 'Renamed' };
      (api.put as ReturnType<typeof vi.fn>).mockResolvedValue({ data: updated });

      const result = await updateServer(1, { name: 'Renamed' });

      expect(api.put).toHaveBeenCalledWith('/api/servers/1', { name: 'Renamed' });
      expect(result).toEqual(updated);
    });
  });
});
