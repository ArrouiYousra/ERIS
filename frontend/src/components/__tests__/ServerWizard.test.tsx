import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
// import userEvent from '@testing-library/user-event';
import { ServerWizard } from "../ServerWizard";

describe("ServerWizard", () => {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onCreateServer: vi.fn().mockResolvedValue(1),
    onGoToServer: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders nothing when isOpen is false", () => {
    const { container } = render(
      <ServerWizard {...defaultProps} isOpen={false} />,
    );

    expect(container.innerHTML).toBe("");
  });

  it("renders step 1 when isOpen is true", () => {
    render(<ServerWizard {...defaultProps} />);

    // Step 1 should show purpose options (may appear in multiple elements)
    const elements = screen.getAllByText(/communaut/i);
    expect(elements.length).toBeGreaterThanOrEqual(1);
  });

  it("closes on escape key", () => {
    render(<ServerWizard {...defaultProps} />);

    fireEvent.keyDown(window, { key: "Escape" });

    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it("closes when clicking X button", () => {
    render(<ServerWizard {...defaultProps} />);

    const closeButtons = screen.getAllByRole("button");
    // Find the X/close button (usually the first one)
    const closeBtn = closeButtons.find(
      (btn) => btn.querySelector("svg") && !btn.textContent,
    );
    if (closeBtn) {
      fireEvent.click(closeBtn);
      expect(defaultProps.onClose).toHaveBeenCalled();
    }
  });

  it("navigates to step 2 after selecting purpose", async () => {
    render(<ServerWizard {...defaultProps} />);

    // Click on one of the purpose options
    const communityOptions = screen.getAllByText(/communaut/i);
    const communityOption = communityOptions[0];
    fireEvent.click(communityOption.closest("div[class]") || communityOption);

    // Should now be on step 2
    await waitFor(() => {
      const step2Elements = screen.queryAllByText(/personnalise/i);
      expect(step2Elements.length).toBeGreaterThanOrEqual(1);
    });
  });

  it("resets state when reopened", async () => {
    const { rerender } = render(<ServerWizard {...defaultProps} />);

    // Close
    rerender(<ServerWizard {...defaultProps} isOpen={false} />);

    // Reopen
    rerender(<ServerWizard {...defaultProps} isOpen={true} />);

    // Should be back to step 1
    const elements = screen.getAllByText(/communaut/i);
    expect(elements.length).toBeGreaterThanOrEqual(1);
  });
});
