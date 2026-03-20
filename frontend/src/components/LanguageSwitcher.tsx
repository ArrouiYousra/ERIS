import { useTranslation } from "react-i18next";

export function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const buttonStyle = (active: boolean): React.CSSProperties => ({
    padding: "4px 8px",
    borderRadius: "4px",
    fontSize: "12px",
    cursor: "pointer",
    border: "none",
    backgroundColor: active ? "#5865F2" : "#2b2d31",
    color: active ? "#ffffff" : "#9ca3af",
  });

  return (
    <div style={{ display: "flex", gap: "4px" }}>
      <button onClick={() => i18n.changeLanguage("fr")} style={buttonStyle(i18n.language.startsWith("fr"))}>
        FR
      </button>
      <button onClick={() => i18n.changeLanguage("en")} style={buttonStyle(i18n.language.startsWith("en"))}>
        EN
      </button>
    </div>
  );
}