import { useEffect, useState } from "react";
import { Header } from "../components/Header";
import erisIcone from "../assets/eris_icone.png";
import "../styles/home.css";

export function HomePage() {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <div className={`home-page ${mounted ? "home-page--visible" : ""}`}>
      <Header />
      {/* Hero Section */}
      <section className="home-hero">
        <div className="home-hero-background">
          <div className="home-hero-blob home-hero-blob--1"></div>
          <div className="home-hero-blob home-hero-blob--2"></div>
          <div className="home-hero-blob home-hero-blob--3"></div>
        </div>
        <div className="home-hero-content">
          <img
            src={erisIcone}
            alt="Eris"
            className="home-hero-icon"
          />
          <h1 className="home-hero-title">
            Bienvenue sur <span className="home-hero-title-highlight">Eris</span>
          </h1>
          <p className="home-hero-subtitle">
            Un espace où tes conversations prennent vie. Rejoins tes amis, crée ta communauté et discute sans limites.
          </p>
        </div>
      </section>
    </div>
  );
}
