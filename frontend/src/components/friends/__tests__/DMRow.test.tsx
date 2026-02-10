import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { DMRow } from "../DMRow";
import type { DM } from "../../../types/friends";

const mockDM: DM = {
  id: "dm1",
  friend: {
    id: "1",
    displayName: "Alice",
    username: "alice",
    status: "online",
    avatarColor: "#5865F2",
  },
  lastMessagePreview: "Salut",
  unread: true,
};

describe("DMRow", () => {
  it("renders friend name and message preview", () => {
    render(<DMRow dm={mockDM} onClick={vi.fn()} />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
    expect(screen.getByText("Salut")).toBeInTheDocument();
  });

  it("calls onClick when clicked", async () => {
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(<DMRow dm={mockDM} onClick={onClick} />);
    await user.click(screen.getByRole("button"));
    expect(onClick).toHaveBeenCalled();
  });

  it("applies selected styles when selected", () => {
    const { container } = render(<DMRow dm={mockDM} selected onClick={vi.fn()} />);
    const button = container.querySelector("button");
    expect(button?.className).toContain("text-[#f2f3f5]");
  });

  it("does not show status dot for offline friends", () => {
    const offlineDM: DM = {
      ...mockDM,
      friend: { ...mockDM.friend, status: "offline" },
    };
    const { container } = render(<DMRow dm={offlineDM} onClick={vi.fn()} />);
    // The Avatar has aria-hidden, but the status dot span should not exist
    // For offline, there should be no status dot with border class
    const statusDots = container.querySelectorAll("span.rounded-full.border-2");
    expect(statusDots.length).toBe(0);
  });

  it("does not show unread indicator when not unread", () => {
    const readDM: DM = { ...mockDM, unread: false };
    render(<DMRow dm={readDM} onClick={vi.fn()} />);
    // No unread dot should exist
    const buttons = screen.getAllByRole("button");
    expect(buttons).toHaveLength(1);
  });
});
