import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../client", () => ({
  api: {
    post: vi.fn(),
  },
}));

import { api } from "../client";
import { createInvitation, joinWithInvitation } from "../invitationApi";

describe("invitationApi", () => {
  beforeEach(() => vi.clearAllMocks());

  it("createInvitation calls POST /api/servers/:id/invite", async () => {
    const mockData = { code: "abc123", expiresAt: "2026-12-31" };
    (api.post as any).mockResolvedValue({ data: mockData });

    const result = await createInvitation(5);

    expect(api.post).toHaveBeenCalledWith("/api/servers/5/invite");
    expect(result).toEqual(mockData);
  });

  it("joinWithInvitation calls POST /api/servers/join", async () => {
    const mockData = { serverId: 1, serverName: "Test", message: "Joined" };
    (api.post as any).mockResolvedValue({ data: mockData });

    const result = await joinWithInvitation("abc123");

    expect(api.post).toHaveBeenCalledWith("/api/servers/join", { code: "abc123" });
    expect(result).toEqual(mockData);
  });
});
