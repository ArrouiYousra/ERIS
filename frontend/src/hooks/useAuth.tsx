import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { login as loginApi, signup as signupApi } from "../api/authApi";
import { api } from "../api/client";

export interface AuthUser {
  id: number;
  email?: string;
  username?: string;
  displayName?: string;
}

type AuthContextValue = {
  user: AuthUser | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (
    email: string,
    username: string,
    password: string,
    displayName: string,
    birthDate: string,
  ) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function useProvideAuth(): AuthContextValue {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  // ── Fetch /api/auth/me au reload pour récupérer le user avec son id ──
  useEffect(() => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      setLoading(false);
      return;
    }
    api
      .get<AuthUser>("/api/auth/me")
      .then((response: { data: AuthUser }) => setUser(response.data))
      .catch(() => {
        localStorage.removeItem("access_token");
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const login = async (email: string, password: string) => {
    // loginApi retourne { token, expiresIn } (LoginResponseDTO)
    const data = await loginApi({ email, password });

    if (data.token) {
      localStorage.setItem("access_token", data.token);
    }

    // Fetch le user complet via /me maintenant qu'on a le token
    const { data: userData } = await api.get<AuthUser>("/api/auth/me");
    setUser(userData);
  };

  const signup = async (
    email: string,
    username: string,
    password: string,
    displayName: string,
    birthDate: string,
  ) => {
    await signupApi({
      email,
      username,
      password,
      displayName,
      birthDate,
    });
    // On se connecte automatiquement après l'inscription
    await login(email, password);
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    setUser(null);
  };

  const isAuthenticated = !!user;

  return {
    user,
    loading,
    isAuthenticated,
    login,
    signup,
    logout,
  };
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const value = useProvideAuth();
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}