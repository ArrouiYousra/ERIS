import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useTranslation } from "react-i18next";
import { useAuth } from "../hooks/useAuth";
import { LanguageSwitcher } from "../components/LanguageSwitcher";
import "../styles/auth.css";
import "../styles/login.css";

export function LoginPage() {
  const { t } = useTranslation();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const getErrorMessage = (err: unknown) => {
    if (axios.isAxiosError<{ message?: string }>(err)) {
      return err.response?.data?.message || t("login.error");
    }
    return t("login.error");
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
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <h1 className="auth-title">{t("login.title")}</h1>
            <LanguageSwitcher />
          </div>
          <p className="auth-subtitle">{t("login.subtitle")}</p>
        </div>
        {error && <div className="auth-error">{error}</div>}
        <form className="auth-form" onSubmit={onSubmit}>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="email">{t("login.email")}</label>
            <input id="email" type="email" className="auth-input" value={email}
              onChange={(e) => setEmail(e.target.value)} required disabled={loading} />
          </div>
          <div className="auth-form-group">
            <label className="auth-form-label" htmlFor="password">{t("login.password")}</label>
            <input id="password" type="password" className="auth-input" value={password}
              onChange={(e) => setPassword(e.target.value)} required disabled={loading} />
          </div>
          <button type="submit" className="auth-submit-button" disabled={loading}>
            {loading ? t("login.loading") : t("login.submit")}
          </button>
        </form>
        <div className="auth-footer">
          <span>{t("login.noAccount")} </span>
          <Link to="/signup" className="auth-link">{t("login.register")}</Link>
        </div>
      </div>
    </div>
  );
}