import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ChannelWizard } from '../ChannelWizard';

describe('ChannelWizard', () => {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onCreateChannel: vi.fn().mockResolvedValue(1),
    onGoToChannel: vi.fn(),
    serverMembers: [],
    serverRoles: [],
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders nothing when isOpen is false', () => {
    const { container } = render(
      <ChannelWizard {...defaultProps} isOpen={false} />,
    );

    expect(container.innerHTML).toBe('');
  });

  it('renders step 1 with channel name input', () => {
    render(<ChannelWizard {...defaultProps} />);

    // Step 1 should show channel name input
    expect(screen.getByText(/nom du salon/i)).toBeInTheDocument();
  });

  it('closes on escape key', () => {
    render(<ChannelWizard {...defaultProps} />);

    fireEvent.keyDown(window, { key: 'Escape' });

    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('formats channel name to lowercase with hyphens', async () => {
    render(<ChannelWizard {...defaultProps} />);

    const input = screen.getByPlaceholderText(/nouveau/i) || screen.getByRole('textbox');
    await userEvent.setup().type(input, 'My Channel Name');

    // The input should format the name
    expect(input).toHaveValue('my-channel-name');
  });

  it('creates public channel directly from step 1', async () => {
    render(<ChannelWizard {...defaultProps} />);

    const user = userEvent.setup();
    const input = screen.getByPlaceholderText(/nouveau/i) || screen.getByRole('textbox');
    await user.type(input, 'test');

    // For public channel, there should be a "Créer" button on step 1
    const createButtons = screen.getAllByText(/créer/i);
    await user.click(createButtons[createButtons.length - 1]); // Click the action button (last one)

    await waitFor(() => {
      expect(defaultProps.onCreateChannel).toHaveBeenCalled();
    });
  });

  it('resets state when reopened', () => {
    const { rerender } = render(<ChannelWizard {...defaultProps} />);

    rerender(<ChannelWizard {...defaultProps} isOpen={false} />);
    rerender(<ChannelWizard {...defaultProps} isOpen={true} />);

    // Should be back at step 1 with empty name
    const input = screen.getByPlaceholderText(/nouveau/i) || screen.getByRole('textbox');
    expect(input).toHaveValue('');
  });
});
