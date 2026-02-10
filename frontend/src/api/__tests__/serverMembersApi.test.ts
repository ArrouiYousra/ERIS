import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../client", () => ({
  api: {
    get: vi.fn(),
  },
}));

import { api } from "../client";
import { getServerMember } from "../serverMembersApi";

describe("serverMembersApi", () => {
  beforeEach(() => vi.clearAllMocks());

  it("getServerMember calls GET servers/:id/members", async () => {
    const mockData = [{ id: 1, serverId: 2, userId: 3, nickname: "Alice" }];
    (api.get as any).mockResolvedValue({ data: mockData });

    const result = await getServerMember(2);

    expect(api.get).toHaveBeenCalledWith("servers/2/members");
    expect(result).toEqual(mockData);
  });
});
