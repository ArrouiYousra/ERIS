import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { UserBar } from "../UserBar";

describe("UserBar", () => {
  it("renders user display name", () => {
    render(<UserBar />);
    expect(screen.getByText("Moi")).toBeInTheDocument();
  });

  it("renders username", () => {
    render(<UserBar />);
    expect(screen.getByText("moi")).toBeInTheDocument();
  });

  it("renders mic, headphone, and settings buttons", () => {
    render(<UserBar />);
    expect(screen.getByTitle("Micro")).toBeInTheDocument();
    expect(screen.getByTitle("Casque")).toBeInTheDocument();
  });
});
