import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../hooks/useAuth";
import "../styles/auth.css";
import "../styles/signup.css";

export function SignupPage() {
  const { signup } = useAuth();
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError<{ message?: string; error?: string }>(error)) {
      return (
        error.response?.data?.message ||
        error.response?.data?.error ||
        error.message ||
        "Error during registration. Please try again."
      );
    }
    return "Error during registration. Please try again.";
  };

  const onSubmit: SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setError(null);

    setLoading(true);

    try {
      await signup(email, username, password, displayName || "");
      navigate("/app");
    } catch (err: unknown) {
      console.error("Signup error:", err);
      const errorMessage = getErrorMessage(err);
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page auth-page--signup">
      <div className="auth-background">
        <div className="auth-blob auth-blob--1"></div>
        <div className="auth-blob auth-blob--2"></div>
        <div className="auth-blob auth-blob--3"></div>
      </div>
      <div className="auth-card auth-card--signup">
        <div className="auth-header">
          <h1 className="auth-title">Create an account</h1>
          <p className="auth-subtitle">Join us and start chatting!</p>
        </div>

        {error && <div className="auth-error">{error}</div>}

        <form className="auth-form" onSubmit={onSubmit}>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="email">
              Email <span className="auth-form-label-required">*</span>
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
            <label className="auth-form-label" htmlFor="displayName">
              Display Name
            </label>
            <input
              id="displayName"
              type="text"
              className="auth-input"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              disabled={loading}
            />
          </div>

          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="username">
              Username <span className="auth-form-label-required">*</span>
            </label>
            <input
              id="username"
              type="text"
              className="auth-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="password">
              Password <span className="auth-form-label-required">*</span>
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

          <button
            type="submit"
            className="auth-submit-button"
            disabled={loading}
          >
            {loading ? "Creating account..." : "Sign Up"}
          </button>
        </form>

        <div className="auth-footer">
          <span>Already have an account? </span>
          <Link to="/login" className="auth-link">
            Log in
          </Link>
        </div>
      </div>
    </div>
  );
}
