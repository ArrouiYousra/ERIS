import { describe, it, expect, beforeEach } from "vitest";
import { vi, type Mock } from 'vitest';

vi.mock("../../api/client", () => ({
  api: {
    get: vi.fn(),
  },
}));

vi.mock("@tanstack/react-query", () => ({
  useQuery: vi.fn(),
}));

import { api } from "../../api/client";
import { getMessages, useMessages } from "../useMessages";
import { useQuery } from "@tanstack/react-query";

describe("getMessages", () => {
  beforeEach(() => vi.clearAllMocks());

  it("fetches messages for a channel", async () => {
    const mockData = [{ id: 1, senderId: 1, content: "Hello", createdAt: "2026-01-01" }];
    (api.get as Mock).mockResolvedValue({ data: mockData });

    const result = await getMessages(5);

    expect(api.get).toHaveBeenCalledWith("/api/channels/5/messages");
    expect(result).toEqual(mockData);
  });
});

describe("useMessages", () => {
  it("calls useQuery with correct params when channelId is provided", () => {
    (useQuery as Mock).mockReturnValue({ data: [], isLoading: false });
    useMessages(3);
    expect(useQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: ["messages", 3],
        enabled: true,
      })
    );
  });

  it("disables query when channelId is null", () => {
    (useQuery as any).mockReturnValue({ data: [], isLoading: false });
    useMessages(null);
    expect(useQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: ["messages", null],
        enabled: false,
      })
    );
  });
});
