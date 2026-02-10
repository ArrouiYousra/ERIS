import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { UserBar } from "../UserBar";

const mockLogout = vi.fn();

vi.mock("../../../hooks/useAuth", () => ({
  useAuth: () => ({
    user: { displayName: "John", username: "john42" },
    logout: mockLogout,
  }),
}));

describe("UserBar", () => {
  it("renders user display name", () => {
    render(<UserBar />);
    expect(screen.getByText("John")).toBeInTheDocument();
  });

  it("renders username", () => {
    render(<UserBar />);
    expect(screen.getByText("john42")).toBeInTheDocument();
  });

  it("renders logout button and calls logout on click", async () => {
    const user = userEvent.setup();
    render(<UserBar />);
    const logoutBtn = screen.getByTitle("Se déconnecter");
    expect(logoutBtn).toBeInTheDocument();
    await user.click(logoutBtn);
    expect(mockLogout).toHaveBeenCalled();
  });
});
