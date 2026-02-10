import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ChannelSettings } from '../ChannelSettings';

describe('ChannelSettings', () => {
  const mockChannel = {
    id: 1,
    name: 'general',
    topic: 'General discussion',
    isPrivate: false,
    serverId: 1,
  };

  const defaultProps = {
    isOpen: true,
    channel: mockChannel,
    isPrivate: false,
    onClose: vi.fn(),
    onSave: vi.fn().mockResolvedValue(undefined),
    onDelete: vi.fn().mockResolvedValue(undefined),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders nothing when isOpen is false', () => {
    const { container } = render(
      <ChannelSettings {...defaultProps} isOpen={false} />,
    );

    expect(container.innerHTML).toBe('');
  });

  it('renders nothing when channel is null', () => {
    const { container } = render(
      <ChannelSettings {...defaultProps} channel={null} />,
    );

    expect(container.innerHTML).toBe('');
  });

  it('renders channel settings when open', () => {
    render(<ChannelSettings {...defaultProps} />);

    // "Vue d'ensemble" appears in sidebar + content header
    const elements = screen.getAllByText(/vue d'ensemble/i);
    expect(elements.length).toBeGreaterThanOrEqual(1);
    // "Supprimer le salon" is in the sidebar
    expect(screen.getByText(/supprimer le salon/i)).toBeInTheDocument();
  });

  it('initializes form with channel data', () => {
    render(<ChannelSettings {...defaultProps} />);

    // Check name input is populated
    const inputs = screen.getAllByRole('textbox');
    const nameInput = inputs.find(
      (input) => (input as HTMLInputElement).value === 'general',
    );
    expect(nameInput).toBeDefined();
  });

  it('closes on escape key', () => {
    render(<ChannelSettings {...defaultProps} />);

    fireEvent.keyDown(window, { key: 'Escape' });

    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('shows save bar when changes are made', async () => {
    render(<ChannelSettings {...defaultProps} />);

    const user = userEvent.setup();
    const inputs = screen.getAllByRole('textbox');
    const nameInput = inputs[0]; // First textbox is the name input

    await user.clear(nameInput);
    await user.type(nameInput, 'renamed');

    // Save bar should appear
    await waitFor(() => {
      expect(screen.getByText(/enregistrer/i)).toBeInTheDocument();
    });
  });

  it('calls onSave when save button clicked', async () => {
    render(<ChannelSettings {...defaultProps} />);

    const user = userEvent.setup();
    const inputs = screen.getAllByRole('textbox');

    await user.clear(inputs[0]);
    await user.type(inputs[0], 'renamed');

    await waitFor(() => {
      const saveButton = screen.getByText(/enregistrer/i);
      return user.click(saveButton);
    });

    await waitFor(() => {
      expect(defaultProps.onSave).toHaveBeenCalled();
    });
  });

  it('shows delete confirmation when delete clicked', async () => {
    render(<ChannelSettings {...defaultProps} />);

    const user = userEvent.setup();
    // Click the sidebar delete button (first "Supprimer le salon")
    const deleteButton = screen.getByText(/supprimer le salon/i);
    await user.click(deleteButton);

    await waitFor(() => {
      // Confirmation shows "Es-tu sûr" and "Annuler" button
      expect(screen.getByText(/es-tu sûr/i)).toBeInTheDocument();
    });
  });
});
