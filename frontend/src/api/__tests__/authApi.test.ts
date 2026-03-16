import { describe, it, expect, vi, beforeEach } from 'vitest';
import { login, signup, getMe } from '../authApi';
import { api } from '../client';

vi.mock('../client', () => ({
  api: {
    post: vi.fn(),
    get: vi.fn(),
    interceptors: { request: { use: vi.fn() } },
  },
}));

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('login', () => {
    it('sends POST to /api/auth/login with payload', async () => {
      const mockData = { token: 'jwt-token', expiresIn: 3600 };
      (api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: mockData });

      const result = await login({ email: 'test@example.com', password: 'Password1' });

      expect(api.post).toHaveBeenCalledWith('/api/auth/login', {
        email: 'test@example.com',
        password: 'Password1',
      });
      expect(result).toEqual(mockData);
    });

    it('throws on network error', async () => {
      (api.post as ReturnType<typeof vi.fn>).mockRejectedValue(new Error('Network Error'));

      await expect(login({ email: 'a@b.c', password: 'x' })).rejects.toThrow('Network Error');
    });
  });

  describe('signup', () => {
    it('sends POST to /api/auth/signup with payload', async () => {
      const mockData = { id: 1, email: 'test@example.com' };
      (api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: mockData });

      const result = await signup({
        email: 'test@example.com',
        password: 'Password1',
        username: 'testuser',
        displayName: 'Test',
      });

      expect(api.post).toHaveBeenCalledWith('/api/auth/signup', {
        email: 'test@example.com',
        password: 'Password1',
        username: 'testuser',
        displayName: 'Test',
      });
      expect(result).toEqual(mockData);
    });
  });

  describe('getMe', () => {
    it('throws "not implemented" error', async () => {
      await expect(getMe()).rejects.toThrow('Endpoint /api/me not implemented yet');
    });
  });
});
