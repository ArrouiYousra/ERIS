import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useTranslation } from "react-i18next";
import { useAuth } from "../hooks/useAuth";
import { LanguageSwitcher } from "../components/LanguageSwitcher";
import "../styles/auth.css";
import "../styles/signup.css";

export function SignupPage() {
  const { t } = useTranslation();
  const { signup } = useAuth();
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const getErrorMessage = (err: unknown) => {
    if (axios.isAxiosError<{ message?: string; error?: string }>(err)) {
      return err.response?.data?.message || err.response?.data?.error || t("signup.error");
    }
    return t("signup.error");
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
      setError(getErrorMessage(err));
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
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <h1 className="auth-title">{t("signup.title")}</h1>
            <LanguageSwitcher />
          </div>
          <p className="auth-subtitle">{t("signup.subtitle")}</p>
        </div>
        {error && <div className="auth-error">{error}</div>}
        <form className="auth-form" onSubmit={onSubmit}>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="email">
              {t("signup.email")} <span className="auth-form-label-required">*</span>
            </label>
            <input id="email" type="email" className="auth-input" value={email}
              onChange={(e) => setEmail(e.target.value)} required disabled={loading} />
          </div>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="displayName">{t("signup.displayName")}</label>
            <input id="displayName" type="text" className="auth-input" value={displayName}
              onChange={(e) => setDisplayName(e.target.value)} disabled={loading} />
          </div>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="username">
              {t("signup.username")} <span className="auth-form-label-required">*</span>
            </label>
            <input id="username" type="text" className="auth-input" value={username}
              onChange={(e) => setUsername(e.target.value)} required disabled={loading} />
          </div>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="password">
              {t("signup.password")} <span className="auth-form-label-required">*</span>
            </label>
            <input id="password" type="password" className="auth-input" value={password}
              onChange={(e) => setPassword(e.target.value)} required disabled={loading} />
          </div>
          <button type="submit" className="auth-submit-button" disabled={loading}>
            {loading ? t("signup.loading") : t("signup.submit")}
          </button>
        </form>
        <div className="auth-footer">
          <span>{t("signup.hasAccount")} </span>
          <Link to="/login" className="auth-link">{t("signup.login")}</Link>
        </div>
      </div>
    </div>
  );
}