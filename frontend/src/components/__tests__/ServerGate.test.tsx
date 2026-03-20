import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ServerGate } from "../ServerGate";

describe("ServerGate", () => {
  const defaultProps = {
    onCreateServer: vi.fn(),
    onJoinServer: vi.fn(),
  };

  beforeEach(() => vi.clearAllMocks());

  // Helper to click the "Rejoindre un serveur" choice button
  const clickJoinButton = async (user: ReturnType<typeof userEvent.setup>) => {
    // The choice button contains a span.server-gate-choice-label with exact text
    const buttons = screen.getAllByRole("button");
    const joinBtn = buttons.find((btn) =>
      btn.textContent?.includes("Rejoindre un serveur")
    );
    await user.click(joinBtn!);
  };

  it("renders choice step by default", () => {
    render(<ServerGate {...defaultProps} />);
    expect(screen.getByText(/créer un serveur/i)).toBeInTheDocument();
    expect(screen.getByText(/rejoindre un serveur/i)).toBeInTheDocument();
  });

  it("navigates to create step", async () => {
    const user = userEvent.setup();
    render(<ServerGate {...defaultProps} />);
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    expect(screen.getByPlaceholderText("Mon super serveur")).toBeInTheDocument();
  });

  it("navigates to join step", async () => {
    const user = userEvent.setup();
    render(<ServerGate {...defaultProps} />);
    await clickJoinButton(user);
    expect(screen.getByPlaceholderText("Collez le lien d'invitation")).toBeInTheDocument();
  });

  it("shows error when joining with empty link", async () => {
    const user = userEvent.setup();
    render(<ServerGate {...defaultProps} />);
    await clickJoinButton(user);
    const form = screen.getByPlaceholderText("Collez le lien d'invitation").closest("form")!;
    fireEvent.submit(form);
    expect(screen.getByText(/collez le lien/i)).toBeInTheDocument();
  });

  it("calls onJoinServer with link", async () => {
    defaultProps.onJoinServer.mockResolvedValue(undefined);
    const user = userEvent.setup();
    render(<ServerGate {...defaultProps} />);
    await clickJoinButton(user);
    await user.type(screen.getByPlaceholderText("Collez le lien d'invitation"), "abc123");
    const form = screen.getByPlaceholderText("Collez le lien d'invitation").closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(defaultProps.onJoinServer).toHaveBeenCalledWith("abc123");
    });
  });

  it("shows error when joining server fails", async () => {
    defaultProps.onJoinServer.mockRejectedValue(new Error("Lien invalide"));
    const user = userEvent.setup();
    render(<ServerGate {...defaultProps} />);
    await clickJoinButton(user);
    await user.type(screen.getByPlaceholderText("Collez le lien d'invitation"), "badcode");
    const form = screen.getByPlaceholderText("Collez le lien d'invitation").closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(screen.getByText(/lien invalide/i)).toBeInTheDocument();
    });
  });
});
