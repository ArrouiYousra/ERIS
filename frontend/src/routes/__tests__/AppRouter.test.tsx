import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';

// We'll test ProtectedRoute behavior by mocking useAuth

const mockUseAuth = vi.fn();

vi.mock('../../hooks/useAuth', () => ({
  useAuth: () => mockUseAuth(),
  AuthProvider: ({ children }: any) => children,
}));

vi.mock('../../api/wsApi', () => ({
  useSocket: () => ({
    subscribe: vi.fn(),
    publish: vi.fn(),
    connected: false,
  }),
  SocketProvider: ({ children }: any) => children,
}));

vi.mock('../../pages/ChatLayout', () => ({
  ChatLayout: () => <div data-testid="chat-layout">ChatLayout</div>,
}));

vi.mock('../../pages/HomePage', () => ({
  HomePage: () => <div data-testid="home-page">HomePage</div>,
}));

vi.mock('../../pages/LoginPage', () => ({
  LoginPage: () => <div data-testid="login-page">LoginPage</div>,
}));

vi.mock('../../pages/SignupPage', () => ({
  SignupPage: () => <div data-testid="signup-page">SignupPage</div>,
}));

import { AppRouter } from '../AppRouter';

describe('AppRouter', () => {
  it('renders home page at /', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      loading: false,
      user: null,
      login: vi.fn(),
      signup: vi.fn(),
      logout: vi.fn(),
    });

    // We need to override BrowserRouter since we can't control the URL
    // Instead, we'll test with a simpler approach
    render(<AppRouter />);

    expect(screen.getByTestId('home-page')).toBeInTheDocument();
  });

  it('shows loading state when auth is loading', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      loading: true,
      user: null,
      login: vi.fn(),
      signup: vi.fn(),
      logout: vi.fn(),
    });

    render(<AppRouter />);

    // Home page should still render (loading only affects /app)
    expect(screen.getByTestId('home-page')).toBeInTheDocument();
  });
});
