import { useState } from "react";
import "../styles/serverGate.css";

export type ServerModalStep = "choice" | "create" | "join";

interface ServerGateProps {
  onCreateServer: (name: string) => Promise<void>;
  onJoinServer: (inviteLink: string) => Promise<void>;
}

export function ServerGate({ onCreateServer, onJoinServer }: ServerGateProps) {
  const [step, setStep] = useState<ServerModalStep>("choice");
  const [serverName, setServerName] = useState("");
  const [inviteLink, setInviteLink] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleBack = () => {
    setStep("choice");
    setServerName("");
    setInviteLink("");
    setError(null);
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!serverName.trim()) {
      setError("Entrez un nom de serveur");
      return;
    }

    setIsSubmitting(true);

    try {
      await onCreateServer(serverName.trim());
    } catch {
      setError("Impossible de créer le serveur");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleJoinSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!inviteLink.trim()) {
      setError("Collez le lien d'invitation");
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      await onJoinServer(inviteLink.trim()); // ✅ backend call
      setInviteLink("");
      // Optional: show a toast or alert: "Vous avez rejoint le serveur !"
    } catch (err: unknown) {
      const error = err as { message?: string };
      console.error(error);
      setError(error?.message || "Lien invalide ou expiré");
    }  finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="server-gate">
      {/* ================= CHOICE ================= */}
      {step === "choice" && (
        <>
          <h2 className="server-gate-title">Créer ou rejoindre un serveur</h2>

          <p className="server-gate-subtitle">
            Crée ton propre espace ou rejoins une communauté existante
          </p>

          <div className="server-gate-choices">
            <button
              className="server-gate-choice"
              onClick={() => setStep("create")}
            >
              <span className="server-gate-choice-icon">🚀</span>

              <span className="server-gate-choice-label">Créer un serveur</span>

              <span className="server-gate-choice-desc">
                Lance ta propre communauté
              </span>
            </button>

            <button
              className="server-gate-choice"
              onClick={() => setStep("join")}
            >
              <span className="server-gate-choice-icon">🔗</span>

              <span className="server-gate-choice-label">
                Rejoindre un serveur
              </span>

              <span className="server-gate-choice-desc">
                Utilise un lien d’invitation
              </span>
            </button>
          </div>
        </>
      )}

      {/* ================= CREATE ================= */}
      {step === "create" && (
        <>
          <button className="server-gate-back" onClick={handleBack}>
            ← Retour
          </button>

          <h2 className="server-gate-title">Créer un serveur</h2>

          <form className="server-gate-form" onSubmit={handleCreateSubmit}>
            <label className="server-gate-label">Nom du serveur</label>

            <input
              className="server-gate-input"
              value={serverName}
              onChange={(e) => setServerName(e.target.value)}
              placeholder="Mon super serveur"
            />

            {error && <p className="server-gate-error">{error}</p>}

            <button className="server-gate-submit" disabled={isSubmitting}>
              {isSubmitting ? "Création..." : "Créer"}
            </button>
          </form>
        </>
      )}

      {/* ================= JOIN ================= */}
      {step === "join" && (
        <>
          <button className="server-gate-back" onClick={handleBack}>
            ← Retour
          </button>

          <h2 className="server-gate-title">Rejoindre un serveur</h2>

          <form className="server-gate-form" onSubmit={handleJoinSubmit}>
            <label className="server-gate-label">Lien d’invitation</label>

            <input
              className="server-gate-input"
              value={inviteLink}
              onChange={(e) => setInviteLink(e.target.value)}
              placeholder="https://..."
            />

            {error && <p className="server-gate-error">{error}</p>}

            <button className="server-gate-submit" disabled={isSubmitting}>
              {isSubmitting ? "Connexion..." : "Rejoindre"}
            </button>
          </form>
        </>
      )}
    </div>
  );
}
