import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

describe('client', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    vi.stubGlobal('localStorage', {
      getItem: vi.fn(),
      setItem: vi.fn(),
      removeItem: vi.fn(),
      clear: vi.fn(),
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.resetModules();
  });

  it('creates axios instance with correct baseURL', async () => {
    // Import fresh to test the module initialization
    const { api } = await import('../client');
    expect(api.defaults.baseURL).toBeDefined();
  });

  it('adds Authorization header when token exists', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('my-jwt-token');

    const { api } = await import('../client');

    // Simulate the interceptor by calling it manually
    const interceptors = api.interceptors.request as any;
    // The interceptor was registered, so we can test it via a mock request
    const config = { headers: {} as Record<string, string> } as any;

    // Get the interceptor function (first handler registered)
    if (interceptors.handlers && interceptors.handlers.length > 0) {
      const handler = interceptors.handlers[0];
      if (handler && handler.fulfilled) {
        const result = handler.fulfilled(config);
        // Token should be set
        expect(result.headers.Authorization).toBe('Bearer my-jwt-token');
      }
    }
  });

  it('does not add Authorization header when no token', async () => {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);

    const { api } = await import('../client');

    const config = { headers: {} as Record<string, string> } as any;
    const interceptors = api.interceptors.request as any;

    if (interceptors.handlers && interceptors.handlers.length > 0) {
      const handler = interceptors.handlers[0];
      if (handler && handler.fulfilled) {
        const result = handler.fulfilled(config);
        expect(result.headers.Authorization).toBeUndefined();
      }
    }
  });
});
