import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { ServerMembers } from "../ServersMembers";

vi.mock("../../hooks/useServers", () => ({
  useServerMember: vi.fn(),
}));

import { useServerMember } from "../../hooks/useServers";

describe("ServerMembers", () => {
  it("shows loading state", () => {
    (useServerMember as any).mockReturnValue({ data: [], isLoading: true });
    render(<ServerMembers serverId={1} />);
    expect(screen.getByText("Loading members...")).toBeInTheDocument();
  });

  it("shows no members message", () => {
    (useServerMember as any).mockReturnValue({ data: [], isLoading: false });
    render(<ServerMembers serverId={1} />);
    expect(screen.getByText("No members found")).toBeInTheDocument();
  });

  it("renders member list", () => {
    (useServerMember as any).mockReturnValue({
      data: [
        { id: 1, serverId: 1, userId: 1, nickname: "Alice", roleId: 1, typingStatus: false, joinedAt: "" },
        { id: 2, serverId: 1, userId: 2, nickname: "Bob", roleId: 1, typingStatus: false, joinedAt: "" },
      ],
      isLoading: false,
    });
    render(<ServerMembers serverId={1} />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
    expect(screen.getByText("Bob")).toBeInTheDocument();
  });
});
