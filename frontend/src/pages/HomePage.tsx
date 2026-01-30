import { useEffect, useState } from "react";
import { Header } from "../components/Header";
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
      <section className="hero">
        <div className="hero-background">
          <div className="hero-blob hero-blob--1"></div>
          <div className="hero-blob hero-blob--2"></div>
          <div className="hero-blob hero-blob--3"></div>
        </div>
        <div className="hero-content">
        </div>
      </section>
    </div>
  );
}
