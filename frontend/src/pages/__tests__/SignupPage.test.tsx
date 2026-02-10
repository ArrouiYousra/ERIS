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
    expect(button).toBeDisabled();
  });

  it('shows error when terms not accepted on submit', () => {
    renderSignup();

    // Without filling anything, the button should be disabled since terms are not accepted
    const button = screen.getByRole('button', { name: 'Sign Up' });
    expect(button).toBeDisabled();
  });

  it('shows error when birth date is incomplete', async () => {
    renderSignup();

    const user = userEvent.setup();

    // Fill form
    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Display Name/), 'Test');
    await user.type(screen.getByLabelText(/Username/), 'testuser');
    await user.type(screen.getByLabelText(/Password/), 'Pass1234');

    // Accept terms
    const checkbox = screen.getByRole('checkbox');
    await user.click(checkbox);

    // Submit form directly (bypassing HTML5 validation since birth selects are required)
    const form = screen.getByRole('button', { name: 'Sign Up' }).closest('form')!;
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Please enter your complete date of birth.')).toBeInTheDocument();
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

    // Accept terms
    const checkbox = screen.getByRole('checkbox');
    await user.click(checkbox);

    // Select birth date
    const selects = screen.getAllByRole('combobox');
    await user.selectOptions(selects[0], '01'); // Month
    await user.selectOptions(selects[1], '15'); // Day
    await user.selectOptions(selects[2], '2000'); // Year

    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith(
        'test@example.com',
        'testuser',
        'Pass1234',
        'Test',
        '2000-01-15',
      );
      expect(mockNavigate).toHaveBeenCalledWith('/app');
    });
  });

  it('shows error on signup failure', async () => {
    mockSignup.mockRejectedValue({
      response: { data: { message: 'Email already used' } },
    });
    renderSignup();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/Email/), 'test@example.com');
    await user.type(screen.getByLabelText(/Display Name/), 'T');
    await user.type(screen.getByLabelText(/Username/), 'u');
    await user.type(screen.getByLabelText(/Password/), 'P1');

    const checkbox = screen.getByRole('checkbox');
    await user.click(checkbox);

    const selects = screen.getAllByRole('combobox');
    await user.selectOptions(selects[0], '01');
    await user.selectOptions(selects[1], '01');
    await user.selectOptions(selects[2], '2000');

    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    await waitFor(() => {
      expect(screen.getByText('Email already used')).toBeInTheDocument();
    });
  });
});
