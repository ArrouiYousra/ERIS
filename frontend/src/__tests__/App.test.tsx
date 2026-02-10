import { describe, it, expect, vi } from "vitest";
import { render } from "@testing-library/react";

// Mock AppRouter since it has complex dependencies
vi.mock("../routes/AppRouter", () => ({
  AppRouter: () => <div data-testid="app-router">Router</div>,
}));

import App from "../App";

describe("App", () => {
  it("renders AppRouter", () => {
    const { getByTestId } = render(<App />);
    expect(getByTestId("app-router")).toBeInTheDocument();
  });
});
