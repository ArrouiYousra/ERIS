import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { Header } from "../Header";

describe("Header", () => {
  it("renders logo and login link", () => {
    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    );

    expect(screen.getByText("εris")).toBeInTheDocument();
    expect(screen.getByText("Log in")).toBeInTheDocument();
  });

  it("logo links to home", () => {
    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    );

    const logoLink = screen.getByText("εris").closest("a");
    expect(logoLink).toHaveAttribute("href", "/");
  });

  it("login links to /login", () => {
    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    );

    const loginLink = screen.getByText("Log in").closest("a");
    expect(loginLink).toHaveAttribute("href", "/login");
  });
});
