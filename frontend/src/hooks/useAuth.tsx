import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { login as loginApi, signup as signupApi, getMe } from "../api/authApi";

type AuthContextValue = {
  user: any | null;
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
  const [user, setUser] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      setLoading(false);
      return;
    }
    // Pour l'instant, on ne peut pas récupérer l'utilisateur avec /me
    // car le backend n'a pas encore cet endpoint avec auth
    // On considère l'utilisateur comme authentifié s'il a un token
    // On crée un objet user minimal pour maintenir l'authentification
    setUser({ id: null, authenticated: true });
    setLoading(false);
    // TODO: Implémenter /api/me dans le backend avec authentification
  }, []);

  const login = async (email: string, password: string) => {
    const data = await loginApi({ email, password });

    // store the token
    if (data.token) {
      localStorage.setItem("access_token", data.token);
    }

    // set user
    setUser(data.user);
  };

  const signup = async (
    email: string,
    username: string,
    password: string,
    displayName: string,
    birthDate: string,
  ) => {
    const userData = await signupApi({
      email,
      username,
      password,
      displayName,
      birthDate,
    });
    // Le backend retourne directement le UserResponseDTO après création
    // On se connecte automatiquement après l'inscription
    await login(email, password);
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    setUser(null);
  };

  // On considère l'utilisateur comme authentifié s'il a un token OU un user
  const token =
    typeof window !== "undefined" ? localStorage.getItem("access_token") : null;
  const isAuthenticated = !!user || !!token;

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
