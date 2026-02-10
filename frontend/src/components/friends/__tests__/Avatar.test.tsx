import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Avatar } from "../Avatar";

describe("Avatar", () => {
  it("renders initial uppercase", () => {
    render(<Avatar initial="a" color="#5865F2" />);
    expect(screen.getByText("A")).toBeInTheDocument();
  });

  it("renders with sm size", () => {
    const { container } = render(<Avatar initial="b" color="#ff0000" size="sm" />);
    const div = container.firstChild as HTMLElement;
    expect(div.style.width).toBe("32px");
    expect(div.style.height).toBe("32px");
  });

  it("renders with lg size", () => {
    const { container } = render(<Avatar initial="c" color="#00ff00" size="lg" />);
    const div = container.firstChild as HTMLElement;
    expect(div.style.width).toBe("48px");
    expect(div.style.height).toBe("48px");
  });

  it("renders children instead of initial", () => {
    render(<Avatar initial="d" color="#0000ff"><span>Custom</span></Avatar>);
    expect(screen.getByText("Custom")).toBeInTheDocument();
  });

  it("applies background color", () => {
    const { container } = render(<Avatar initial="e" color="#abcdef" />);
    const div = container.firstChild as HTMLElement;
    expect(div.style.backgroundColor).toBe("rgb(171, 205, 239)");
  });

  it("defaults to md size", () => {
    const { container } = render(<Avatar initial="f" color="#111" />);
    const div = container.firstChild as HTMLElement;
    expect(div.style.width).toBe("40px");
  });
});
