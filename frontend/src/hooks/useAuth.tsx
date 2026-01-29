import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import {
  login as loginApi,
  signup as signupApi,
  getMe,
} from "../api/authApi";

type AuthContextValue = {
  user: any | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, username: string, password: string) => Promise<void>;
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
    getMe()
      .then(setUser)
      .finally(() => setLoading(false));
  }, []);

  const login = async (email: string, password: string) => {
    const data = await loginApi({ email, password });
    // Tout dépend de ce que vous faites dans le back
    localStorage.setItem("access_token", data.accessToken ?? data.token);
    const me = await getMe();
    setUser(me);
  };

  const signup = async (email: string, username: string, password: string) => {
    await signupApi({ email, username, password });
    await login(email, password);
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    setUser(null);
  };

  return {
    user,
    loading,
    isAuthenticated: !!user,
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

