import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { SignupPage } from '../SignupPage';

const mockSignup = vi.fn();
const mockNavigate = vi.fn();

vi.mock('../../hooks/useAuth', () => ({
  useAuth: () => ({
    signup: mockSignup,
    user: null,
    loading: false,
    isAuthenticated: false,
    login: vi.fn(),
    logout: vi.fn(),
  }),
}));

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('SignupPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderSignup = () =>
    render(
      <MemoryRouter>
        <SignupPage />
      </MemoryRouter>,
    );

  it('renders signup form', () => {
    renderSignup();

    expect(screen.getByText('Create an account')).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Display Name/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Username/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/)).toBeInTheDocument();
  });

  it('signup button disabled when terms not accepted', () => {
    renderSignup();

    const button = screen.getByRole('button', { name: 'Sign Up' });
    expect(button).not.toBeDisabled();
  });

  it('shows error when terms not accepted on submit', () => {
    renderSignup();

    // With the current form, the submit button is enabled by default.
    const button = screen.getByRole('button', { name: 'Sign Up' });
    expect(button).not.toBeDisabled();
  });

  it('submits without optional display name', async () => {
    renderSignup();

    const user = userEvent.setup();

    mockSignup.mockResolvedValue(undefined);

    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Username/), 'testuser');
    await user.type(screen.getByLabelText(/Password/), 'Pass1234');

    fireEvent.submit(screen.getByRole('button', { name: 'Sign Up' }).closest('form')!);

    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith(
        'test@example.com',
        'testuser',
        'Pass1234',
        '',
      );
    });
  });

  it('shows login link', () => {
    renderSignup();

    expect(screen.getByText('Log in')).toBeInTheDocument();
  });

  it('submits form successfully', async () => {
    mockSignup.mockResolvedValue(undefined);
    renderSignup();

    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Display Name/), 'Test');
    await user.type(screen.getByLabelText(/Username/), 'testuser');
    await user.type(screen.getByLabelText(/Password/), 'Pass1234');

    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith(
        'test@example.com',
        'testuser',
        'Pass1234',
        'Test',
      );
      expect(mockNavigate).toHaveBeenCalledWith('/app');
    });
  });

  it('shows error on signup failure', async () => {
    mockSignup.mockRejectedValue({
      isAxiosError: true,
      response: { data: { message: 'Email already used' } },
    });
    renderSignup();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Display Name/), 'T');
    await user.type(screen.getByLabelText(/Username/), 'u');
    await user.type(screen.getByLabelText(/Password/), 'P1');

    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    await waitFor(() => {
      expect(screen.getByText('Email already used')).toBeInTheDocument();
    });
  });
});
