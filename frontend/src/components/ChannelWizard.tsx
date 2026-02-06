import React, { useState, useEffect } from "react";
import { X, Hash, Lock, Users, AtSign, Shield, ArrowLeft, ArrowRight, Check } from "lucide-react";

export interface ChannelWizardData {
  name: string;
  isPrivate: boolean;
  allowedMembers: string[];
  allowedRoles: string[];
}

interface ChannelWizardProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateChannel: (data: ChannelWizardData) => Promise<number | null>;
  onGoToChannel: (channelId: number) => void;
  serverMembers?: { id: number; username: string }[];
  serverRoles?: { id: number; name: string }[];
}

const TOTAL_STEPS = 2;

export function ChannelWizard({
  isOpen,
  onClose,
  onCreateChannel,
  onGoToChannel,
  serverMembers = [],
  serverRoles = [],
}: ChannelWizardProps) {
  const [step, setStep] = useState(1);
  const [data, setData] = useState<ChannelWizardData>({
    name: "",
    isPrivate: false,
    allowedMembers: [],
    allowedRoles: [],
  });
  const [isCreating, setIsCreating] = useState(false);
  const [memberSearch, setMemberSearch] = useState("");

  // Reset state when modal opens
  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setIsCreating(false);
      setMemberSearch("");
      setData({
        name: "",
        isPrivate: false,
        allowedMembers: [],
        allowedRoles: [],
      });
    }
  }, [isOpen]);

  // Handle escape key
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) onClose();
    };
    window.addEventListener("keydown", handleEscape);
    return () => window.removeEventListener("keydown", handleEscape);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const goNext = () => {
    if (step < TOTAL_STEPS) {
      setStep((s) => s + 1);
    }
  };

  const goBack = () => {
    if (step > 1) {
      setStep((s) => s - 1);
    }
  };

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // Format channel name: lowercase, replace spaces with hyphens
    const formatted = e.target.value.toLowerCase().replace(/\s+/g, "-").replace(/[^a-z0-9-]/g, "");
    setData((d) => ({ ...d, name: formatted }));
  };

  const toggleMember = (username: string) => {
    setData((d) => ({
      ...d,
      allowedMembers: d.allowedMembers.includes(username)
        ? d.allowedMembers.filter((m) => m !== username)
        : [...d.allowedMembers, username],
    }));
  };

  const toggleRole = (roleName: string) => {
    setData((d) => ({
      ...d,
      allowedRoles: d.allowedRoles.includes(roleName)
        ? d.allowedRoles.filter((r) => r !== roleName)
        : [...d.allowedRoles, roleName],
    }));
  };

  const handleCreate = async () => {
    setIsCreating(true);
    try {
      const channelId = await onCreateChannel(data);
      if (channelId) {
        onGoToChannel(channelId);
      }
      onClose();
    } catch (error) {
      console.error("Failed to create channel:", error);
    } finally {
      setIsCreating(false);
    }
  };

  const canProceedStep1 = data.name.trim().length >= 1;

  const filteredMembers = serverMembers.filter((m) =>
    m.username.toLowerCase().includes(memberSearch.toLowerCase().replace("@", ""))
  );

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm animate-fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-md bg-[#313338] rounded-xl shadow-2xl overflow-hidden animate-scale-in"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-10 p-1.5 rounded-full text-gray-400 hover:text-white hover:bg-white/10 transition-colors"
          aria-label="Fermer"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Step content with animations */}
        <div className="relative min-h-[380px] overflow-hidden">
          {/* Step 1: Channel Name & Privacy */}
          <div
            className={`absolute inset-0 p-5 pt-8 pb-4 flex flex-col transition-all duration-300 ease-out ${
              step === 1
                ? "opacity-100 translate-x-0 scale-100"
                : "opacity-0 -translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center mb-3">
              <h2 className="text-lg font-bold text-white mb-1">Créer un salon</h2>
              <p className="text-gray-400 text-xs">
                Les salons sont l'endroit où vous communiquez.
              </p>
            </div>

            {/* Channel Type */}
            <div className="mb-3">
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">
                Type de salon
              </label>
              <div className="space-y-1.5">
                <button
                  onClick={() => setData((d) => ({ ...d, isPrivate: false }))}
                  className={`w-full flex items-center gap-2 p-2.5 rounded-md border transition-all ${
                    !data.isPrivate
                      ? "bg-[#5865F2]/20 border-[#5865F2]"
                      : "bg-[#2b2d31] border-transparent hover:bg-[#404249]"
                  }`}
                >
                  <Hash className={`w-4 h-4 ${!data.isPrivate ? "text-[#5865F2]" : "text-gray-400"}`} />
                  <div className="text-left flex-1">
                    <p className="text-white text-sm font-medium">Salon textuel</p>
                  </div>
                  {!data.isPrivate && <Check className="w-4 h-4 text-[#5865F2]" />}
                </button>

                <button
                  onClick={() => setData((d) => ({ ...d, isPrivate: true }))}
                  className={`w-full flex items-center gap-2 p-2.5 rounded-md border transition-all ${
                    data.isPrivate
                      ? "bg-[#5865F2]/20 border-[#5865F2]"
                      : "bg-[#2b2d31] border-transparent hover:bg-[#404249]"
                  }`}
                >
                  <Lock className={`w-4 h-4 ${data.isPrivate ? "text-[#5865F2]" : "text-gray-400"}`} />
                  <div className="text-left flex-1">
                    <p className="text-white text-sm font-medium">Salon privé</p>
                    <p className="text-gray-400 text-[10px]">Seuls les membres sélectionnés pourront voir ce salon</p>
                  </div>
                  {data.isPrivate && <Check className="w-4 h-4 text-[#5865F2]" />}
                </button>
              </div>
            </div>

            {/* Channel Name Input */}
            <div className="mb-3">
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-1.5">
                Nom du salon
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                  {data.isPrivate ? <Lock className="w-4 h-4" /> : <Hash className="w-4 h-4" />}
                </span>
                <input
                  type="text"
                  value={data.name}
                  onChange={handleNameChange}
                  placeholder="nouveau-salon"
                  className="w-full pl-10 pr-4 py-2 bg-[#1e1f22] border border-transparent focus:border-[#5865F2] rounded-md text-white placeholder-gray-500 outline-none transition-colors text-sm"
                />
              </div>
            </div>

            {/* Navigation buttons */}
            <div className="flex gap-3 mt-auto pt-4">
              <button
                onClick={onClose}
                className="flex-1 px-4 py-2.5 text-gray-300 hover:text-white hover:bg-white/5 rounded-md transition-colors"
              >
                Annuler
              </button>
              {data.isPrivate ? (
                <button
                  onClick={goNext}
                  disabled={!canProceedStep1}
                  className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-[#5865F2] hover:bg-[#4752C4] disabled:bg-[#5865F2]/50 disabled:cursor-not-allowed text-white font-medium rounded-md transition-colors"
                >
                  Suivant
                  <ArrowRight className="w-4 h-4" />
                </button>
              ) : (
                <button
                  onClick={handleCreate}
                  disabled={!canProceedStep1 || isCreating}
                  className="flex-1 px-4 py-2.5 bg-[#5865F2] hover:bg-[#4752C4] disabled:bg-[#5865F2]/50 disabled:cursor-not-allowed text-white font-medium rounded-md transition-colors"
                >
                  {isCreating ? "Création..." : "Créer le salon"}
                </button>
              )}
            </div>
          </div>

          {/* Step 2: Add Members & Roles (only for private channels) */}
          <div
            className={`absolute inset-0 p-5 pt-8 pb-4 flex flex-col transition-all duration-300 ease-out ${
              step === 2
                ? "opacity-100 translate-x-0 scale-100"
                : "opacity-0 translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center mb-3">
              <h2 className="text-lg font-bold text-white mb-1">Ajouter des membres</h2>
              <p className="text-gray-400 text-xs">
                Qui peut accéder à <span className="text-white font-medium">#{data.name}</span> ?
              </p>
            </div>

            {/* Roles Section */}
            <div className="mb-3">
              <div className="flex items-center gap-2 mb-1.5">
                <Shield className="w-3.5 h-3.5 text-gray-400" />
                <span className="text-xs font-semibold text-gray-300 uppercase">Rôles</span>
              </div>
              {serverRoles.length > 0 ? (
                <div className="flex flex-wrap gap-1.5">
                  {serverRoles.map((role) => (
                    <button
                      key={role.id}
                      onClick={() => toggleRole(role.name)}
                      className={`px-2.5 py-1 rounded-full text-xs font-medium transition-all ${
                        data.allowedRoles.includes(role.name)
                          ? "bg-[#5865F2] text-white"
                          : "bg-[#2b2d31] text-gray-300 hover:bg-[#404249]"
                      }`}
                    >
                      {role.name}
                    </button>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-xs italic">Tu n'as pas encore créé de rôles</p>
              )}
            </div>

            {/* Members Section */}
            <div className="flex-1 min-h-0">
              <div className="flex items-center gap-2 mb-1.5">
                <Users className="w-3.5 h-3.5 text-gray-400" />
                <span className="text-xs font-semibold text-gray-300 uppercase">Membres</span>
              </div>
              
              {/* Search input */}
              <div className="relative mb-2">
                <AtSign className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
                <input
                  type="text"
                  value={memberSearch}
                  onChange={(e) => setMemberSearch(e.target.value)}
                  placeholder="Rechercher un membre..."
                  className="w-full pl-9 pr-4 py-1.5 bg-[#1e1f22] border border-transparent focus:border-[#5865F2] rounded-md text-white placeholder-gray-500 outline-none transition-colors text-xs"
                />
              </div>

              {/* Members list */}
              <div className="max-h-[80px] overflow-y-auto space-y-0.5">
                {serverMembers.length > 0 ? (
                  filteredMembers.map((member) => (
                    <button
                      key={member.id}
                      onClick={() => toggleMember(member.username)}
                      className={`w-full flex items-center gap-2 p-1.5 rounded-md transition-all ${
                        data.allowedMembers.includes(member.username)
                          ? "bg-[#5865F2]/20"
                          : "hover:bg-[#404249]"
                      }`}
                    >
                      <div className="w-6 h-6 rounded-full bg-[#5865F2] flex items-center justify-center text-white text-xs font-medium">
                        {member.username.charAt(0).toUpperCase()}
                      </div>
                      <span className="text-white text-xs flex-1 text-left">{member.username}</span>
                      {data.allowedMembers.includes(member.username) && (
                        <Check className="w-3.5 h-3.5 text-[#5865F2]" />
                      )}
                    </button>
                  ))
                ) : (
                  <p className="text-gray-500 text-xs italic text-center py-1">
                    Aucun membre à ajouter
                  </p>
                )}
              </div>
            </div>

            {/* Selected count */}
            {(data.allowedMembers.length > 0 || data.allowedRoles.length > 0) && (
              <p className="text-xs text-gray-400 mt-1">
                {data.allowedMembers.length + data.allowedRoles.length} sélectionné(s)
              </p>
            )}

            {/* Navigation buttons */}
            <div className="flex gap-3 mt-auto pt-4">
              <button
                onClick={goBack}
                className="flex items-center justify-center gap-1 px-4 py-2.5 text-gray-300 hover:text-white hover:bg-white/5 rounded-md transition-colors"
              >
                <ArrowLeft className="w-4 h-4" />
                Retour
              </button>
              <button
                onClick={handleCreate}
                disabled={isCreating}
                className="flex-1 px-4 py-2.5 bg-[#5865F2] hover:bg-[#4752C4] disabled:bg-[#5865F2]/50 disabled:cursor-not-allowed text-white font-medium rounded-md transition-colors"
              >
                {isCreating ? "Création..." : "Créer le salon"}
              </button>
            </div>

            {/* Skip option */}
            <button
              onClick={handleCreate}
              disabled={isCreating}
              className="mt-2 text-sm text-gray-400 hover:text-gray-300 transition-colors disabled:opacity-50"
            >
              Passer cette étape
            </button>
          </div>
        </div>

        {/* Progress dots */}
        {data.isPrivate && (
          <div className="flex justify-center gap-2 pb-4">
            {Array.from({ length: TOTAL_STEPS }).map((_, i) => (
              <div
                key={i}
                onClick={() => {
                  if (i + 1 < step) setStep(i + 1);
                }}
                className={`w-2 h-2 rounded-full transition-all duration-300 ${
                  i + 1 === step
                    ? "w-6 bg-[#5865F2]"
                    : i + 1 < step
                    ? "bg-[#5865F2]/50 hover:bg-[#5865F2]/70 cursor-pointer"
                    : "bg-gray-600"
                }`}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
