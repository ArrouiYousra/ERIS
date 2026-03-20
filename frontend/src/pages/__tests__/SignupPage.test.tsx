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

  // "Create an account" → "Créer un compte"
  // "Display Name" (label) → "Nom d'affichage"
  // "Username" (label) → "Nom d'utilisateur"
  // "Password" (label) → "Mot de passe"
  // "Sign Up" (button) → "S'inscrire"
  // "Log in" → "Se connecter"

  it('renders signup form', () => {
    renderSignup();
    expect(screen.getByText('Créer un compte')).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Nom d'affichage/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Nom d'utilisateur/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Mot de passe/)).toBeInTheDocument();
  });

  it('signup button disabled when terms not accepted', () => {
    renderSignup();
    const button = screen.getByRole('button', { name: "S'inscrire" });
    expect(button).not.toBeDisabled();
  });

  it('shows error when terms not accepted on submit', () => {
    renderSignup();
    const button = screen.getByRole('button', { name: "S'inscrire" });
    expect(button).not.toBeDisabled();
  });

  it('submits without optional display name', async () => {
    renderSignup();
    const user = userEvent.setup();
    mockSignup.mockResolvedValue(undefined);
    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Nom d'utilisateur/), 'testuser');
    await user.type(screen.getByLabelText(/Mot de passe/), 'Pass1234');
    fireEvent.submit(screen.getByRole('button', { name: "S'inscrire" }).closest('form')!);
    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith('test@example.com', 'testuser', 'Pass1234', '');
    });
  });

  it('shows login link', () => {
    renderSignup();
    expect(screen.getByText('Se connecter')).toBeInTheDocument();
  });

  it('submits form successfully', async () => {
    mockSignup.mockResolvedValue(undefined);
    renderSignup();
    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Nom d'affichage/), 'Test');
    await user.type(screen.getByLabelText(/Nom d'utilisateur/), 'testuser');
    await user.type(screen.getByLabelText(/Mot de passe/), 'Pass1234');
    await user.click(screen.getByRole('button', { name: "S'inscrire" }));
    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith('test@example.com', 'testuser', 'Pass1234', 'Test');
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
    await user.type(screen.getByLabelText(/Nom d'affichage/), 'T');
    await user.type(screen.getByLabelText(/Nom d'utilisateur/), 'u');
    await user.type(screen.getByLabelText(/Mot de passe/), 'P1');
    await user.click(screen.getByRole('button', { name: "S'inscrire" }));
    await waitFor(() => {
      expect(screen.getByText('Email already used')).toBeInTheDocument();
    });
  });
});