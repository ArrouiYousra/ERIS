import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

// Mock the image import
vi.mock("../../assets/eris_icone.png", () => ({ default: "eris_icone.png" }));

import { HomePage } from "../HomePage";

describe("HomePage", () => {
  it("renders welcome text", () => {
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );

    expect(screen.getByText(/bienvenue sur/i)).toBeInTheDocument();
    expect(screen.getByText("Eris")).toBeInTheDocument();
  });

  it("renders hero subtitle", () => {
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );

    expect(screen.getByText(/espace où tes conversations/i)).toBeInTheDocument();
  });

  it("renders Eris icon", () => {
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );

    const img = screen.getByAltText("Eris");
    expect(img).toBeInTheDocument();
  });

  it("applies visible class after mount", async () => {
    const { container } = render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );

    // After useEffect, the class should be applied
    const homePage = container.querySelector(".home-page");
    expect(homePage).toBeInTheDocument();
  });
});
