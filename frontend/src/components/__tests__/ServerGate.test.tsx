import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ServerGate } from "../ServerGate";
import { MemoryRouter } from "react-router-dom";

describe("ServerGate", () => {
  const defaultProps = {
    onCreateServer: vi.fn(),
    onJoinServer: vi.fn(),
  };

  beforeEach(() => vi.clearAllMocks());

  const renderComponent = () => {
    return render(
      <MemoryRouter>
        <ServerGate {...defaultProps} />
      </MemoryRouter>,
    );
  };

  // Helper to click the "Rejoindre un serveur" choice button
  const clickJoinButton = async (user: ReturnType<typeof userEvent.setup>) => {
    // The choice button contains a span.server-gate-choice-label with exact text
    const buttons = screen.getAllByRole("button");
    const joinBtn = buttons.find((btn) =>
      btn.textContent?.includes("Rejoindre un serveur"),
    );
    await user.click(joinBtn!);
  };

  it("renders choice step by default", () => {
    renderComponent();
    expect(screen.getByText(/créer un serveur/i)).toBeInTheDocument();
    expect(screen.getByText(/rejoindre un serveur/i)).toBeInTheDocument();
  });

  it("navigates to create step", async () => {
    const user = userEvent.setup();
    renderComponent();
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    expect(
      screen.getByPlaceholderText("Mon super serveur"),
    ).toBeInTheDocument();
  });

  it("navigates to join step", async () => {
    const user = userEvent.setup();
    renderComponent();
    await clickJoinButton(user);
    expect(screen.getByPlaceholderText("https://...")).toBeInTheDocument();
  });

  it("goes back from create step", async () => {
    const user = userEvent.setup();
    renderComponent();
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    await user.click(screen.getByText(/retour/i));
    expect(screen.getByText(/créer un serveur/i)).toBeInTheDocument();
    expect(screen.getByText(/rejoindre un serveur/i)).toBeInTheDocument();
  });

  it("shows error when creating with empty name", async () => {
    renderComponent();
    const user = userEvent.setup();
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    const form = screen
      .getByPlaceholderText("Mon super serveur")
      .closest("form")!;
    fireEvent.submit(form);
    expect(screen.getByText(/entrez un nom/i)).toBeInTheDocument();
  });

  it("calls onCreateServer with name", async () => {
    defaultProps.onCreateServer.mockResolvedValue(undefined);
    const user = userEvent.setup();
    renderComponent();
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    await user.type(
      screen.getByPlaceholderText("Mon super serveur"),
      "TestServer",
    );
    const form = screen
      .getByPlaceholderText("Mon super serveur")
      .closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(defaultProps.onCreateServer).toHaveBeenCalledWith("TestServer");
    });
  });

  it("shows error when creating server fails", async () => {
    defaultProps.onCreateServer.mockRejectedValue(new Error("fail"));
    const user = userEvent.setup();
    renderComponent();
    const createButtons = screen.getAllByText(/créer un serveur/i);
    await user.click(createButtons[0]);
    await user.type(
      screen.getByPlaceholderText("Mon super serveur"),
      "TestServer",
    );
    const form = screen
      .getByPlaceholderText("Mon super serveur")
      .closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(screen.getByText(/impossible de créer/i)).toBeInTheDocument();
    });
  });

  it("shows error when joining with empty link", async () => {
    const user = userEvent.setup();
    renderComponent();
    await clickJoinButton(user);
    const form = screen.getByPlaceholderText("https://...").closest("form")!;
    fireEvent.submit(form);
    expect(screen.getByText(/collez le lien/i)).toBeInTheDocument();
  });

  it("calls onJoinServer with link", async () => {
    defaultProps.onJoinServer.mockResolvedValue(undefined);
    const user = userEvent.setup();
    renderComponent();
    await clickJoinButton(user);
    await user.type(screen.getByPlaceholderText("https://..."), "abc123");
    const form = screen.getByPlaceholderText("https://...").closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(defaultProps.onJoinServer).toHaveBeenCalledWith("abc123");
    });
  });

  it("shows error when joining server fails", async () => {
    defaultProps.onJoinServer.mockRejectedValue(new Error("Lien invalide"));
    const user = userEvent.setup();
    renderComponent();
    await clickJoinButton(user);
    await user.type(screen.getByPlaceholderText("https://..."), "badcode");
    const form = screen.getByPlaceholderText("https://...").closest("form")!;
    fireEvent.submit(form);
    await waitFor(() => {
      expect(screen.getByText(/lien invalide/i)).toBeInTheDocument();
    });
  });
});
