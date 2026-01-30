import { type FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "../styles/login.css";

export function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await login(email, password);
      navigate("/");
    } catch (err: any) {
      setError(err?.response?.data?.message || "Erreur de connexion. Vérifiez vos identifiants.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-background">
        <div className="login-blob login-blob--1"></div>
        <div className="login-blob login-blob--2"></div>
        <div className="login-blob login-blob--3"></div>
      </div>
      <div className="login-container">
        <div className="login-header">
          <h1 className="login-title">Welcome back!</h1>
          <p className="login-subtitle">We're so excited to see you again!</p>
        </div>

        {error && <div className="login-error">{error}</div>}

        <form className="login-form" onSubmit={onSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email
            </label>
            <input
              id="email"
              type="email"
              className="form-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? "Connexion..." : "Log In"}
          </button>
        </form>

        <div className="login-footer">
          <span>Need an account? </span>
          <Link to="/signup" className="login-link">
            Register
          </Link>
        </div>
      </div>
    </div>
  );
}
