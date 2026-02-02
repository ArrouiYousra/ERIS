import { useState } from "react";
import "../styles/serverModal.css";

export type ServerModalStep = "choice" | "create" | "join";

interface ServerModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateServer: (name: string) => Promise<void>;
  onJoinServer: (inviteLink: string) => Promise<void>;
}

export function ServerModal({
  isOpen,
  onClose,
  onCreateServer,
  onJoinServer,
}: ServerModalProps) {
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

  const handleClose = () => {
    onClose();
    setStep("choice");
    setServerName("");
    setInviteLink("");
    setError(null);
    setIsSubmitting(false);
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!serverName.trim()) {
      setError("Entrez un nom de serveur");
      return;
    }
    setError(null);
    setIsSubmitting(true);
    try {
      await onCreateServer(serverName.trim());
      handleClose();
    } catch (err) {
      setError("Impossible de créer le serveur. Réessayez.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleJoinSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const link = inviteLink.trim();
    if (!link) {
      setError("Collez le lien d'invitation");
      return;
    }
    setError(null);
    setIsSubmitting(true);
    try {
      await onJoinServer(link);
      handleClose();
    } catch (err) {
      setError("Lien invalide ou expiré. Réessayez.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="server-modal-overlay" onClick={handleClose}>
      <div
        className="server-modal"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-labelledby="server-modal-title"
      >
        <button
          type="button"
          className="server-modal-close"
          onClick={handleClose}
          aria-label="Fermer"
        >
          ×
        </button>

        {step === "choice" && (
          <>
            <h2 id="server-modal-title" className="server-modal-title">
              Créer ou rejoindre un serveur
            </h2>
            <p className="server-modal-subtitle">
              Choisissez une option pour continuer
            </p>
            <div className="server-modal-choices">
              <button
                type="button"
                className="server-modal-choice"
                onClick={() => setStep("create")}
              >
                <span className="server-modal-choice-icon">➕</span>
                <span className="server-modal-choice-label">Créer un serveur</span>
                <span className="server-modal-choice-desc">
                  Créez votre propre serveur pour discuter avec vos amis
                </span>
              </button>
              <button
                type="button"
                className="server-modal-choice"
                onClick={() => setStep("join")}
              >
                <span className="server-modal-choice-icon">🔗</span>
                <span className="server-modal-choice-label">
                  Rejoindre un serveur
                </span>
                <span className="server-modal-choice-desc">
                  Rejoignez un serveur avec un lien d&apos;invitation
                </span>
              </button>
            </div>
          </>
        )}

        {step === "create" && (
          <>
            <button
              type="button"
              className="server-modal-back"
              onClick={handleBack}
              aria-label="Retour"
            >
              ← Retour
            </button>
            <h2 id="server-modal-title" className="server-modal-title">
              Créer un serveur
            </h2>
            <p className="server-modal-subtitle">
              Donnez un nom à votre serveur. Vous pourrez le modifier plus tard.
            </p>
            <form onSubmit={handleCreateSubmit} className="server-modal-form">
              <label htmlFor="server-name" className="server-modal-label">
                Nom du serveur
              </label>
              <input
                id="server-name"
                type="text"
                className="server-modal-input"
                placeholder="Mon super serveur"
                value={serverName}
                onChange={(e) => setServerName(e.target.value)}
                maxLength={100}
                autoFocus
              />
              {error && <p className="server-modal-error">{error}</p>}
              <button
                type="submit"
                className="server-modal-submit"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Création…" : "Créer le serveur"}
              </button>
            </form>
          </>
        )}

        {step === "join" && (
          <>
            <button
              type="button"
              className="server-modal-back"
              onClick={handleBack}
              aria-label="Retour"
            >
              ← Retour
            </button>
            <h2 id="server-modal-title" className="server-modal-title">
              Rejoindre un serveur
            </h2>
            <p className="server-modal-subtitle">
              Entrez un lien d&apos;invitation envoyé par un ami ou une communauté.
            </p>
            <form onSubmit={handleJoinSubmit} className="server-modal-form">
              <label htmlFor="invite-link" className="server-modal-label">
                Lien d&apos;invitation
              </label>
              <input
                id="invite-link"
                type="text"
                className="server-modal-input"
                placeholder="https://... ou code d'invitation"
                value={inviteLink}
                onChange={(e) => setInviteLink(e.target.value)}
                autoFocus
              />
              {error && <p className="server-modal-error">{error}</p>}
              <button
                type="submit"
                className="server-modal-submit"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Rejoindre…" : "Rejoindre le serveur"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}
