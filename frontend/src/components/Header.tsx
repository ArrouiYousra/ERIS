import { Link } from "react-router-dom";
import "../styles/header.css";

export function Header() {
  return (
    <header className="site-header">
      <div className="header-container">
        <Link to="/" className="header-logo">
          <span className="header-logo-text">εris</span>
        </Link>
        <nav className="header-nav">
          <Link to="/login" className="header-button">
            Log in
          </Link>
        </nav>
      </div>
    </header>
  );
}
