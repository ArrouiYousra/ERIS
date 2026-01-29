import { api } from "./client";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SignupPayload {
  email: string;
  password: string;
  username: string;
}

export async function login(payload: LoginPayload) {
  const { data } = await api.post("/auth/login", payload);
  return data; // token + user (selon le back)
}

export async function signup(payload: SignupPayload) {
  const { data } = await api.post("/auth/signup", payload);
  return data;
}

export async function getMe() {
  const { data } = await api.get("/me");
  return data;
}