import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../hooks/useAuth";
import "../styles/auth.css";
import "../styles/login.css";

export function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError<{ message?: string }>(error)) {
      return error.response?.data?.message || "Erreur de connexion. Vérifiez vos identifiants.";
    }
    return "Erreur de connexion. Vérifiez vos identifiants.";
  };

  const onSubmit: SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await login(email, password);
      navigate("/app");
    } catch (err: unknown) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page auth-page--login">
      <div className="auth-background">
        <div className="auth-blob auth-blob--1"></div>
        <div className="auth-blob auth-blob--2"></div>
        <div className="auth-blob auth-blob--3"></div>
      </div>
      <div className="auth-card auth-card--login">
        <div className="auth-header">
          <h1 className="auth-title">Welcome back!</h1>
          <p className="auth-subtitle">We're so excited to see you again!</p>
        </div>

        {error && <div className="auth-error">{error}</div>}

        <form className="auth-form" onSubmit={onSubmit}>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="email">
              Email
            </label>
            <input
              id="email"
              type="email"
              className="auth-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              type="password"
              className="auth-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <button type="submit" className="auth-submit-button" disabled={loading}>
            {loading ? "Connexion..." : "Log In"}
          </button>
        </form>

        <div className="auth-footer">
          <span>Need an account? </span>
          <Link to="/signup" className="auth-link">
            Register
          </Link>
        </div>
      </div>
    </div>
  );
}
