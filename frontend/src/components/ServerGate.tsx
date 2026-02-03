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
    try {
      await onJoinServer(inviteLink.trim());
    } catch {
      setError("Lien invalide ou expiré");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="server-modal">
      {" "}
      {/* reuse styles */}
      {step === "choice" && (
        <>
          <h2 className="server-modal-title">Créer ou rejoindre un serveur</h2>
          <div className="server-modal-choices">
            <button onClick={() => setStep("create")}>Créer un serveur</button>
            <button onClick={() => setStep("join")}>
              Rejoindre un serveur
            </button>
          </div>
        </>
      )}
      {step === "create" && (
        <>
          <button onClick={handleBack}>← Retour</button>
          <form onSubmit={handleCreateSubmit}>
            <input
              value={serverName}
              onChange={(e) => setServerName(e.target.value)}
              placeholder="Nom du serveur"
            />
            {error && <p>{error}</p>}
            <button disabled={isSubmitting}>Créer</button>
          </form>
        </>
      )}
      {step === "join" && (
        <>
          <button onClick={handleBack}>← Retour</button>
          <form onSubmit={handleJoinSubmit}>
            <input
              value={inviteLink}
              onChange={(e) => setInviteLink(e.target.value)}
              placeholder="Lien d'invitation"
            />
            {error && <p>{error}</p>}
            <button disabled={isSubmitting}>Rejoindre</button>
          </form>
        </>
      )}
    </div>
  );
}
