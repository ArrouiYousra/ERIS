import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ServerBar } from "../ServerBar";

vi.mock("../../../assets/eris_icone.png", () => ({ default: "eris_icon.png" }));

describe("ServerBar", () => {
  const defaultProps = {
    selectedServerId: null as number | null,
    onSelectServer: vi.fn(),
    serverIds: [1, 2],
    serverNames: { 1: "Server One", 2: "Server Two" } as Record<number, string>,
    onAddServer: vi.fn(),
  };

  it("renders DM button", () => {
    render(<ServerBar {...defaultProps} />);
    expect(screen.getByTitle("Message priv\u00e9s")).toBeInTheDocument();
  });

  it("renders server buttons with first letter", () => {
    render(<ServerBar {...defaultProps} />);
    expect(screen.getByTitle("Server One")).toBeInTheDocument();
    expect(screen.getByTitle("Server Two")).toBeInTheDocument();
  });

  it("calls onSelectServer when server clicked", async () => {
    const user = userEvent.setup();
    render(<ServerBar {...defaultProps} />);
    await user.click(screen.getByTitle("Server One"));
    expect(defaultProps.onSelectServer).toHaveBeenCalledWith(1);
  });

  it("calls onAddServer when + clicked", async () => {
    const user = userEvent.setup();
    render(<ServerBar {...defaultProps} />);
    await user.click(screen.getByTitle("Ajouter un serveur"));
    expect(defaultProps.onAddServer).toHaveBeenCalled();
  });

  it("shows active indicator on DM when no server selected", () => {
    render(<ServerBar {...defaultProps} />);
    const dmButton = screen.getByTitle("Message priv\u00e9s");
    expect(dmButton.getAttribute("aria-pressed")).toBe("true");
  });

  it("shows active indicator on selected server", () => {
    render(<ServerBar {...defaultProps} selectedServerId={1} />);
    const serverBtn = screen.getByTitle("Server One");
    expect(serverBtn.getAttribute("aria-pressed")).toBe("true");
  });
});
