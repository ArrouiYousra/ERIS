import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

const mockUseServerMember = vi.fn();
const mockUsePresence = vi.fn();

vi.mock("../../../hooks/useServers", () => ({
  useServerMember: (...args: any[]) => mockUseServerMember(...args),
}));

vi.mock("../../../hooks/usePresence", () => ({
  usePresence: (...args: any[]) => mockUsePresence(...args),
}));

import { LeftPanel } from "../LeftPanel";

describe("LeftPanel", () => {
  it("renders collapsed state", () => {
    mockUseServerMember.mockReturnValue({ data: [], isLoading: false });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set() });
    render(<LeftPanel serverId={1} collapsed onToggle={vi.fn()} />);
    expect(screen.getByLabelText(/ouvrir le panneau gauche/i)).toBeInTheDocument();
  });

  it("calls onToggle when collapsed panel clicked", async () => {
    mockUseServerMember.mockReturnValue({ data: [], isLoading: false });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set() });
    const user = userEvent.setup();
    const onToggle = vi.fn();
    render(<LeftPanel serverId={1} collapsed onToggle={onToggle} />);
    await user.click(screen.getByLabelText(/ouvrir le panneau gauche/i));
    expect(onToggle).toHaveBeenCalled();
  });

  it("renders expanded state with members", () => {
    mockUseServerMember.mockReturnValue({
      data: [
        { userId: 1, nickname: "Alice" },
        { userId: 2, nickname: "Bob" },
      ],
      isLoading: false,
    });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set([1]) });

    render(<LeftPanel serverId={1} />);
    expect(screen.getByText("Membres")).toBeInTheDocument();
    expect(screen.getByText("Alice")).toBeInTheDocument();
    expect(screen.getByText("Bob")).toBeInTheDocument();
  });

  it("shows online/offline sections", () => {
    mockUseServerMember.mockReturnValue({
      data: [
        { userId: 1, nickname: "Alice" },
        { userId: 2, nickname: "Bob" },
      ],
      isLoading: false,
    });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set([1]) });

    render(<LeftPanel serverId={1} />);
    expect(screen.getByText(/en ligne/i)).toBeInTheDocument();
    expect(screen.getByText(/hors ligne/i)).toBeInTheDocument();
  });

  it("shows loading state", () => {
    mockUseServerMember.mockReturnValue({ data: [], isLoading: true });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set() });
    render(<LeftPanel serverId={1} />);
    expect(screen.getByText(/chargement des membres/i)).toBeInTheDocument();
  });

  it("shows no members found when empty", () => {
    mockUseServerMember.mockReturnValue({ data: [], isLoading: false });
    mockUsePresence.mockReturnValue({ onlineUserIds: new Set() });
    render(<LeftPanel serverId={1} />);
    expect(screen.getByText(/aucun membre/i)).toBeInTheDocument();
  });
});
