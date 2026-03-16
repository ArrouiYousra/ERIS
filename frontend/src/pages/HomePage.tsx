import { useEffect, useRef } from "react";
import { Header } from "../components/Header";
import erisIcone from "../assets/eris_icone.png";
import "../styles/home.css";

export function HomePage() {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    containerRef.current?.classList.add("home-page--visible");
  }, []);

  return (
    <div ref={containerRef} className="home-page">
      <Header />
      <section className="hero">
        <div className="hero-background">
          <div className="hero-blob hero-blob--1"></div>
          <div className="hero-blob hero-blob--2"></div>
          <div className="hero-blob hero-blob--3"></div>
        </div>
        <div className="hero-content">
          <img src={erisIcone} alt="Eris" className="hero-icon" />
          <h1 className="hero-title">
            Bienvenue sur <span className="hero-title-highlight">Eris</span>
          </h1>
          <p className="hero-subtitle">
            Un espace où tes conversations prennent vie. Rejoins tes amis, crée ta communauté et discute sans limites.
          </p>
        </div>
      </section>
    </div>
  );
}