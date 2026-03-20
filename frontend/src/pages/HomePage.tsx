import { useTranslation } from "react-i18next";
import { Header } from "../components/Header";
import erisIcone from "../assets/eris_icone.png";
import "../styles/home.css";

export function HomePage() {
  const { t } = useTranslation();

  return (
    <div className="home-page home-page--visible">
      <Header />
      <section className="home-hero">
        <div className="home-hero-background">
          <div className="home-hero-blob home-hero-blob--1"></div>
          <div className="home-hero-blob home-hero-blob--2"></div>
          <div className="home-hero-blob home-hero-blob--3"></div>
        </div>
        <div className="home-hero-content">
          <img src={erisIcone} alt="Eris" className="home-hero-icon" />
          <h1 className="home-hero-title">
            {t("home.welcome")} <span className="home-hero-title-highlight">Eris</span>
          </h1>
          <p className="home-hero-subtitle">{t("home.subtitle")}</p>
        </div>
      </section>
    </div>
  );
}