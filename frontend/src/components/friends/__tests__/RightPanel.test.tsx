import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { RightPanel } from "../RightPanel";

describe("RightPanel", () => {
  it("renders collapsed state", () => {
    render(<RightPanel collapsed onToggle={vi.fn()} />);
    expect(screen.getByLabelText(/ouvrir le panneau droit/i)).toBeInTheDocument();
  });

  it("calls onToggle when collapsed panel clicked", async () => {
    const user = userEvent.setup();
    const onToggle = vi.fn();
    render(<RightPanel collapsed onToggle={onToggle} />);
    await user.click(screen.getByLabelText(/ouvrir le panneau droit/i));
    expect(onToggle).toHaveBeenCalled();
  });

  it("renders expanded state with content", () => {
    render(<RightPanel />);
    expect(screen.getByText("En ligne")).toBeInTheDocument();
    expect(screen.getByText(/tout est calme/i)).toBeInTheDocument();
  });

  it("renders thumbs up and thumbs down buttons", () => {
    render(<RightPanel />);
    expect(screen.getByText(/oui, j'en suis/i)).toBeInTheDocument();
    expect(screen.getByText(/non merci/i)).toBeInTheDocument();
  });
});
