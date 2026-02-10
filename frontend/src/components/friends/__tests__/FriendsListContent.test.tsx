import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { FriendsListContent } from "../FriendsListContent";

describe("FriendsListContent", () => {
  it("shows all friends when activeTab is ALL", () => {
    render(<FriendsListContent activeTab="ALL" />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
    expect(screen.getByText("Bob")).toBeInTheDocument();
  });

  it("shows only online friends when activeTab is ONLINE", () => {
    render(<FriendsListContent activeTab="ONLINE" />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
    // Diana is offline so should not be present
    expect(screen.queryByText("Diana")).not.toBeInTheDocument();
  });

  it("shows pending message when activeTab is PENDING", () => {
    render(<FriendsListContent activeTab="PENDING" />);
    expect(screen.getByText(/aucune demande en attente/i)).toBeInTheDocument();
  });

  it("shows blocked message when activeTab is BLOCKED", () => {
    render(<FriendsListContent activeTab="BLOCKED" />);
    expect(screen.getByText(/aucun utilisateur bloqu/i)).toBeInTheDocument();
  });
});
