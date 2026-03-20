import { useState } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";
import "../styles/serverGate.css";

export type ServerModalStep = "choice" | "create" | "join";

interface ServerGateProps {
  onCreateServer: (name: string) => Promise<void>;
  onJoinServer: (inviteLink: string) => Promise<void>;
}

export function ServerGate({ onCreateServer, onJoinServer }: ServerGateProps) {
  const { t } = useTranslation();
  const [step, setStep] = useState<ServerModalStep>("choice");
  const [serverName, setServerName] = useState("");
  const [inviteLink, setInviteLink] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getErrorMessage = (errorValue: unknown) => {
    if (axios.isAxiosError<{ message?: string }>(errorValue)) {
      return errorValue.response?.data?.message || errorValue.message;
    }
    return t("chat.invalidLink");
  };

  const handleBack = () => {
    setStep("choice");
    setServerName("");
    setInviteLink("");
    setError(null);
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!serverName.trim()) {
      setError(t("chat.enterServerName"));
      return;
    }
    setIsSubmitting(true);
    try {
      await onCreateServer(serverName.trim());
    } catch {
      setError(t("chat.createError"));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleJoinSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inviteLink.trim()) {
      setError(t("chat.pasteInvite"));
      return;
    }
    setIsSubmitting(true);
    setError(null);
    try {
      await onJoinServer(inviteLink.trim());
      setInviteLink("");
    } catch (err: unknown) {
      console.error(err);
      setError(getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="server-gate">
      {step === "choice" && (
        <div className="server-gate-choices">
          <button className="server-gate-choice" onClick={() => setStep("create")}>
            <span className="server-gate-choice-label">{t("chat.createServer")}</span>
            <span className="server-gate-choice-desc">{t("chat.createServerDesc")}</span>
          </button>
          <button className="server-gate-choice" onClick={() => setStep("join")}>
            <span className="server-gate-choice-label">{t("chat.joinServer")}</span>
            <span className="server-gate-choice-desc">{t("chat.joinServerDesc")}</span>
          </button>
        </div>
      )}

      {step === "create" && (
        <>
          <button className="server-gate-back" onClick={handleBack}>
            ← {t("chat.back")}
          </button>
          <h2 className="server-gate-title">{t("chat.createServer")}</h2>
          <form className="server-gate-form" onSubmit={handleCreateSubmit}>
            <label className="server-gate-label">{t("chat.serverName")}</label>
            <input className="server-gate-input" value={serverName}
              onChange={(e) => setServerName(e.target.value)} placeholder={t("chat.serverNamePlaceholder")} />
            {error && <p className="server-gate-error">{error}</p>}
            <button className="server-gate-submit" disabled={isSubmitting}>
              {isSubmitting ? t("chat.creating") : t("chat.create")}
            </button>
          </form>
        </>
      )}

      {step === "join" && (
        <>
          <button className="server-gate-back" onClick={handleBack}>
            ← {t("chat.back")}
          </button>
          <h2 className="server-gate-title">{t("chat.joinServer")}</h2>
          <form className="server-gate-form" onSubmit={handleJoinSubmit}>
            <label className="server-gate-label">{t("chat.inviteLink")}</label>
            <input className="server-gate-input" value={inviteLink}
              onChange={(e) => setInviteLink(e.target.value)} placeholder={t("chat.pasteInvite")} />
            {error && <p className="server-gate-error">{error}</p>}
            <button className="server-gate-submit" disabled={isSubmitting}>
              {isSubmitting ? t("chat.joining") : t("chat.join")}
            </button>
          </form>
        </>
      )}
    </div>
  );
}