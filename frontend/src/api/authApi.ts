import { api } from "./client";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SignupPayload {
  email: string;
  password: string;
  username: string;
  displayName?: string;
  birthDate?: string;
}

export async function login(payload: LoginPayload) {
  const { data } = await api.post("/api/auth/login", payload);
  return data; // token + user (selon le back)
}

export async function signup(payload: SignupPayload) {
  const { data } = await api.post("/api/auth/signup", payload);
  return data;
}

export async function getMe() {
  // Note: Le backend a /api/{id} pour l'instant, /me nécessite l'auth
  // On utilisera /me une fois l'auth complète, pour l'instant on peut tester avec un ID
  const { data } = await api.get("/api/me");
  return data;
}