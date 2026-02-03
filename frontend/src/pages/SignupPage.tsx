import { type FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "../styles/signup.css";

export function SignupPage() {
  const { signup } = useAuth();
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [birthDay, setBirthDay] = useState("");
  const [birthMonth, setBirthMonth] = useState("");
  const [birthYear, setBirthYear] = useState("");
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!acceptedTerms) {
      setError("You must accept the terms of service.");
      return;
    }

    if (!birthDay || !birthMonth || !birthYear) {
      setError("Please enter your complete date of birth.");
      return;
    }

    setLoading(true);

    try {
      const birthDate = `${birthYear}-${birthMonth}-${birthDay}`;
      await signup(email, username, password, displayName || "", birthDate);
      navigate("/app");
    } catch (err: any) {
      console.error("Signup error:", err);
      const errorMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Error during registration. Please try again.";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-page">
      <div className="signup-background">
        <div className="signup-blob signup-blob--1"></div>
        <div className="signup-blob signup-blob--2"></div>
        <div className="signup-blob signup-blob--3"></div>
      </div>
      <div className="signup-container">
        <div className="signup-header">
          <h1 className="signup-title">Create an account</h1>
          <p className="signup-subtitle">Join us and start chatting!</p>
        </div>

        {error && <div className="signup-error">{error}</div>}

        <form className="signup-form" onSubmit={onSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email <span className="form-label-required">*</span>
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
            <label className="form-label" htmlFor="displayName">
              Display Name
            </label>
            <input
              id="displayName"
              type="text"
              className="form-input"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="username">
              Username <span className="form-label-required">*</span>
            </label>
            <input
              id="username"
              type="text"
              className="form-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password <span className="form-label-required">*</span>
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

          <div className="form-group">
            <label className="form-label">
              Date of Birth <span className="form-label-required">*</span>
            </label>
            <div className="birthdate-fields">
              <select
                className="form-input form-input--select"
                value={birthMonth}
                onChange={(e) => setBirthMonth(e.target.value)}
                required
                disabled={loading}
              >
                <option value="">Month</option>
                <option value="01">January</option>
                <option value="02">February</option>
                <option value="03">March</option>
                <option value="04">April</option>
                <option value="05">May</option>
                <option value="06">June</option>
                <option value="07">July</option>
                <option value="08">August</option>
                <option value="09">September</option>
                <option value="10">October</option>
                <option value="11">November</option>
                <option value="12">December</option>
              </select>
              <select
                className="form-input form-input--day"
                value={birthDay}
                onChange={(e) => setBirthDay(e.target.value)}
                required
                disabled={loading}
              >
                <option value="">Day</option>
                {Array.from({ length: 31 }, (_, i) => i + 1).map((day) => (
                  <option key={day} value={day.toString().padStart(2, "0")}>
                    {day}
                  </option>
                ))}
              </select>
              <select
                className="form-input form-input--year"
                value={birthYear}
                onChange={(e) => setBirthYear(e.target.value)}
                required
                disabled={loading}
              >
                <option value="">Year</option>
                {Array.from(
                  { length: new Date().getFullYear() - 1899 },
                  (_, i) => new Date().getFullYear() - i,
                ).map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group form-group--checkbox">
            <label className="form-checkbox-label">
              <input
                type="checkbox"
                className="form-checkbox"
                checked={acceptedTerms}
                onChange={(e) => setAcceptedTerms(e.target.checked)}
                disabled={loading}
                required
              />
              <span>I have read and agree to the Terms of Service</span>
            </label>
          </div>

          <button
            type="submit"
            className="signup-button"
            disabled={loading || !acceptedTerms}
          >
            {loading ? "Creating account..." : "Sign Up"}
          </button>
        </form>

        <div className="signup-footer">
          <span>Already have an account? </span>
          <Link to="/login" className="signup-link">
            Log in
          </Link>
        </div>
      </div>
    </div>
  );
}
