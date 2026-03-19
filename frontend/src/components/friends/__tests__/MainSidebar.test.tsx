import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MainSidebar } from "../MainSidebar";

vi.mock("../../../hooks/useAuth", () => ({
  useAuth: () => ({
    user: { displayName: "Test", username: "test" },
    logout: vi.fn(),
  }),
}));

describe("MainSidebar", () => {
  const defaultProps = {
    selectedDMId: null as string | null,
    onSelectDM: vi.fn(),
    onAddDM: vi.fn(),
  };

  it("renders search input", () => {
    render(<MainSidebar {...defaultProps} />);
    expect(
      screen.getByPlaceholderText("Recherche ou lance une conversation"),
    ).toBeInTheDocument();
  });

  //DM Row deleted
  // it("renders DM list", () => {
  //   render(<MainSidebar {...defaultProps} />);
  //   expect(screen.getByText("Alice")).toBeInTheDocument();
  //   expect(screen.getByText("Bob")).toBeInTheDocument();
  // });

  // it("filters DMs by search", async () => {
  //   const user = userEvent.setup();
  //   render(<MainSidebar {...defaultProps} />);
  //   const searchInput = screen.getByPlaceholderText("Recherche ou lance une conversation");
  //   await user.type(searchInput, "alice");
  //   expect(screen.getByText("Alice")).toBeInTheDocument();
  //   expect(screen.queryByText("Bob")).not.toBeInTheDocument();
  // });

  it("shows no results message when search matches nothing", async () => {
    const user = userEvent.setup();
    render(<MainSidebar {...defaultProps} />);
    const searchInput = screen.getByPlaceholderText(
      "Recherche ou lance une conversation",
    );
    await user.type(searchInput, "zzzzz");
    expect(screen.getByText(/aucune conversation/i)).toBeInTheDocument();
  });

  // it("calls onSelectDM when DM clicked", async () => {
  //   const user = userEvent.setup();
  //   const onSelectDM = vi.fn();
  //   render(<MainSidebar {...defaultProps} onSelectDM={onSelectDM} />);
  //   await user.click(screen.getByText("Alice"));
  //   expect(onSelectDM).toHaveBeenCalled();
  // });

  it("calls onAddDM when + button clicked", async () => {
    const user = userEvent.setup();
    const onAddDM = vi.fn();
    render(<MainSidebar {...defaultProps} onAddDM={onAddDM} />);
    await user.click(screen.getByTitle("Nouvelle conversation"));
    expect(onAddDM).toHaveBeenCalled();
  });
});
