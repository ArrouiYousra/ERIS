import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MainContent } from "../MainContent";

describe("MainContent", () => {
  it("renders all tab buttons", () => {
    render(<MainContent activeTab="ADD" onTabChange={vi.fn()} />);
    expect(screen.getByText("En ligne")).toBeInTheDocument();
    expect(screen.getByText("Tous")).toBeInTheDocument();
    expect(screen.getByText("En attente")).toBeInTheDocument();
  });

  it("shows AddScreen when ADD tab is active", () => {
    render(<MainContent activeTab="ADD" onTabChange={vi.fn()} />);
    // AddScreen renders "Saisir un pseudo" placeholder
    expect(screen.getByPlaceholderText("Saisir un pseudo")).toBeInTheDocument();
  });

  it("shows FriendsListContent when non-ADD tab", () => {
    render(<MainContent activeTab="ALL" onTabChange={vi.fn()} />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
  });

  it("calls onTabChange when tab clicked", async () => {
    const user = userEvent.setup();
    const onTabChange = vi.fn();
    render(<MainContent activeTab="ADD" onTabChange={onTabChange} />);
    await user.click(screen.getByText("Tous"));
    expect(onTabChange).toHaveBeenCalledWith("ALL");
  });

  it("calls onTabChange with ADD when Ajouter button clicked", async () => {
    const user = userEvent.setup();
    const onTabChange = vi.fn();
    render(<MainContent activeTab="ALL" onTabChange={onTabChange} />);
    // There are multiple "Ajouter" elements, get all and click the button one
    const addButtons = screen.getAllByText("Ajouter");
    await user.click(addButtons[addButtons.length - 1]);
    expect(onTabChange).toHaveBeenCalledWith("ADD");
  });
});
