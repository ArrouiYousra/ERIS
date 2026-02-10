import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InviteModal } from "../InviteModal";

describe("InviteModal", () => {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onCreateInvite: vi.fn(),
    inviteCode: "abc123",
    setInviteCode: vi.fn(),
    onJoinInvite: vi.fn(),
  };

  it("renders nothing when not open", () => {
    const { container } = render(<InviteModal {...defaultProps} isOpen={false} />);
    expect(container.innerHTML).toBe("");
  });

  it("renders modal when open", () => {
    render(<InviteModal {...defaultProps} />);
    expect(screen.getByText("Server Invites")).toBeInTheDocument();
    expect(screen.getByText("Create Invite")).toBeInTheDocument();
    expect(screen.getByText("Close")).toBeInTheDocument();
  });

  it("calls onCreateInvite when button clicked", async () => {
    const user = userEvent.setup();
    render(<InviteModal {...defaultProps} />);
    await user.click(screen.getByText("Create Invite"));
    expect(defaultProps.onCreateInvite).toHaveBeenCalled();
  });

  it("calls onClose when close button clicked", async () => {
    const user = userEvent.setup();
    render(<InviteModal {...defaultProps} />);
    await user.click(screen.getByText("Close"));
    expect(defaultProps.onClose).toHaveBeenCalled();
  });
});
