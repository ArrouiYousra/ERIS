import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AddScreen } from "../AddScreen";

describe("AddScreen", () => {
  it("renders add friend form", () => {
    render(<AddScreen />);
    expect(screen.getByText("Ajouter")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Saisir un pseudo")).toBeInTheDocument();
  });

  it("submit button is disabled when input is empty", () => {
    render(<AddScreen />);
    const submitBtn = screen.getByRole("button", {
      name: /envoyer une demande/i,
    });
    expect(submitBtn).toBeDisabled();
  });

  it("submit button is enabled when input has text", async () => {
    const user = userEvent.setup();
    render(<AddScreen />);
    const input = screen.getByPlaceholderText("Saisir un pseudo");
    await user.type(input, "TestUser");
    const submitBtn = screen.getByRole("button", {
      name: /envoyer une demande/i,
    });
    expect(submitBtn).not.toBeDisabled();
  });

  it("clears input on form submit", async () => {
    const user = userEvent.setup();
    render(<AddScreen />);
    const input = screen.getByPlaceholderText(
      "Saisir un pseudo",
    ) as HTMLInputElement;
    await user.type(input, "TestUser");
    // Submit form
    const form = input.closest("form")!;
    fireEvent.submit(form);
    expect(input.value).toBe("");
  });

  it("does not clear input on submit when empty", () => {
    render(<AddScreen />);
    const input = screen.getByPlaceholderText(
      "Saisir un pseudo",
    ) as HTMLInputElement;
    const form = input.closest("form")!;
    fireEvent.submit(form);
    expect(input.value).toBe("");
  });

  it("renders explore servers button", () => {
    render(<AddScreen />);
    expect(screen.getByText(/explorer des serveurs/i)).toBeInTheDocument();
  });
});
