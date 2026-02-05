import { api } from "./client";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SignupPayload {
  email: string;
  password: string;
  username: string;
  displayName: string;
  birthDate?: string;
}

export async function login(payload: LoginPayload) {
  const { data } = await api.post("/api/auth/signin", payload);
  return data; // token + user (selon le back)
}

export async function signup(payload: SignupPayload) {
  // Le backend n'attend pas birthDate dans UserRequestDTO, on l'enlève
  const { birthDate, ...payloadWithoutBirthDate } = payload;
  const { data } = await api.post("/api/auth/signup", payloadWithoutBirthDate);
  return data;
}

export async function getMe() {
  // Note: Le backend n'a pas encore /api/me avec authentification
  // Pour l'instant, on ne peut pas récupérer l'utilisateur avec cet endpoint
  // TODO: Implémenter /api/me dans le backend avec authentification JWT
  throw new Error("Endpoint /api/me not implemented yet");
}
