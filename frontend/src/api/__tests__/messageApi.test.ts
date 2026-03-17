import { describe, it, expect, vi, beforeEach } from "vitest";
import { type Mock } from 'vitest';

vi.mock("../client", () => ({
  api: {
    get: vi.fn(),
  },
}));

import { api } from "../client";
import { getMessageByChannel } from "../messageApi";

describe("messageApi", () => {
  beforeEach(() => vi.clearAllMocks());

  it("getMessageByChannel calls GET /api/channels/:id/messages", async () => {
    const mockData = [{ id: 1, content: "Hello", channelId: 3, createdAt: "2026-01-01" }];
    (api.get as Mock).mockResolvedValue({ data: mockData });

    const result = await getMessageByChannel(3);

    expect(api.get).toHaveBeenCalledWith("/api/channels/3/messages");
    expect(result).toEqual(mockData);
  });
});
