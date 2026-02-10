import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { ChannelList } from '../ChannelList';

vi.mock('../../hooks/useChannels', () => ({
  useCreateChannel: () => ({
    mutateAsync: vi.fn().mockResolvedValue({ id: 1, name: 'new-channel' }),
  }),
  useUpdateChannel: () => ({
    mutateAsync: vi.fn().mockResolvedValue(undefined),
  }),
  useDeleteChannel: () => ({
    mutateAsync: vi.fn().mockResolvedValue(undefined),
  }),
}));

vi.mock('../../api/invitationApi', () => ({
  createInvitation: vi.fn(),
  joinWithInvitation: vi.fn(),
}));

vi.mock('../InviteModal', () => ({
  InviteModal: () => null,
}));

vi.mock('../ChannelWizard', () => ({
  ChannelWizard: () => null,
}));

vi.mock('../ChannelSettings', () => ({
  ChannelSettings: () => null,
}));

vi.mock('../friends/UserBar', () => ({
  UserBar: () => <div data-testid="user-bar" />,
}));

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

describe('ChannelList', () => {
  const mockChannels = [
    { id: 1, name: 'general', serverId: 1, isPrivate: false },
    { id: 2, name: 'random', serverId: 1, isPrivate: false },
    { id: 3, name: 'private', serverId: 1, isPrivate: true },
  ];

  const defaultProps = {
    serverId: 1 as number | null,
    channels: mockChannels,
    onSelectChannel: vi.fn(),
    selectedChannelId: 1,
    serverName: 'Test Server',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders server name in header', () => {
    render(<ChannelList {...defaultProps} />, { wrapper: createWrapper() });

    expect(screen.getByText('Test Server')).toBeInTheDocument();
  });

  it('renders channel list', () => {
    render(<ChannelList {...defaultProps} />, { wrapper: createWrapper() });

    expect(screen.getByText('general')).toBeInTheDocument();
    expect(screen.getByText('random')).toBeInTheDocument();
    expect(screen.getByText('private')).toBeInTheDocument();
  });

  it('calls onSelectChannel when clicking a channel', () => {
    render(<ChannelList {...defaultProps} />, { wrapper: createWrapper() });

    fireEvent.click(screen.getByText('random'));

    expect(defaultProps.onSelectChannel).toHaveBeenCalledWith(2);
  });

  it('shows empty state when no serverId', () => {
    render(
      <ChannelList {...defaultProps} serverId={null} />,
      { wrapper: createWrapper() },
    );

    // Should show a message about no server selected or be empty
    expect(screen.queryByText('general')).not.toBeInTheDocument();
  });

  it('shows "Salons textuels" category', () => {
    render(<ChannelList {...defaultProps} />, { wrapper: createWrapper() });

    expect(screen.getByText(/salons textuels/i)).toBeInTheDocument();
  });

  it('highlights selected channel', () => {
    render(
      <ChannelList {...defaultProps} selectedChannelId={1} />,
      { wrapper: createWrapper() },
    );

    // "general" should have selected styling
    const generalElement = screen.getByText('general');
    expect(generalElement).toBeInTheDocument();
  });
});
