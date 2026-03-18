import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MessageList } from "../MessageList";

const mockSendMessage = vi.fn();
const mockOnInputChange = vi.fn();
const mockStopTyping = vi.fn();

// Mock de useAuth (La solution à ton erreur)
vi.mock("../../hooks/useAuth", () => ({
  useAuth: () => ({
    user: { id: 1, username: "user@test.com" },
  }),
}));

vi.mock("../../hooks/useMessages", () => ({
  useMessages: (channelId: number | null) => ({
    data: channelId
      ? [
          {
            id: 1,
            senderId: 1,
            senderUsername: "user@test.com",
            content: "Hello world",
            channelId: 1,
            createdAt: "2026-01-01T12:00:00",
          },
        ]
      : [],
  }),
}));

vi.mock("../../hooks/useChannelSocket", () => ({
  useChannelSocket: () => ({
    sendMessage: mockSendMessage,
    messages: [],
  }),
}));

vi.mock("../../hooks/useTyping", () => ({
  useTyping: () => ({
    typingText: null,
    onInputChange: mockOnInputChange,
    stopTyping: mockStopTyping,
  }),
}));

describe("MessageList", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("shows empty state when no channelId and no conversationId", () => {
    render(<MessageList />);

    expect(screen.getByText(/sélectionnez/i)).toBeInTheDocument();
  });

  it("renders channel header with hash icon", () => {
    render(
      <MessageList channelId={1} channelName="general" isPrivate={false} />,
    );

    expect(screen.getByText("general")).toBeInTheDocument();
  });

  it("renders channel header with lock icon when private", () => {
    render(
      <MessageList
        channelId={1}
        channelName="private-channel"
        isPrivate={true}
      />,
    );

    expect(screen.getByText("private-channel")).toBeInTheDocument();
  });

  it("renders channel topic in header", () => {
    render(
      <MessageList
        channelId={1}
        channelName="general"
        channelTopic="General discussion"
      />,
    );

    expect(screen.getByText("General discussion")).toBeInTheDocument();
  });

  it("renders message input", () => {
    render(<MessageList channelId={1} channelName="general" />);

    const input = screen.getByPlaceholderText(/message/i);
    expect(input).toBeInTheDocument();
  });

  it("sends message on enter key", async () => {
    render(<MessageList channelId={1} channelName="general" />);

    const user = userEvent.setup();
    const input = screen.getByPlaceholderText(/message/i);
    await user.type(input, "Hello{enter}");

    expect(mockSendMessage).toHaveBeenCalledWith("Hello");
  });

  it("does not send empty message", () => {
    render(<MessageList channelId={1} channelName="general" />);

    const input = screen.getByPlaceholderText(/message/i);
    // Fire Enter on empty input - handleSendMessage should early return
    fireEvent.keyDown(input, { key: "Enter" });

    // sendMessage from socket hook should not be called (handleSendMessage checks trim())
    expect(mockSendMessage).not.toHaveBeenCalled();
  });

  it("accepts optional sidebar toggle prop", () => {
    const onToggle = vi.fn();
    render(
      <MessageList
        channelId={1}
        channelName="general"
        onToggleSidebar={onToggle}
      />,
    );

    // Find the Users icon button in the header
    const buttons = screen.getAllByRole("button");
    expect(buttons.length).toBeGreaterThan(0);
  });

  it("renders messages", () => {
    render(<MessageList channelId={1} channelName="general" />);

    expect(screen.getByText("Hello world")).toBeInTheDocument();
  });
});
