import { useState, useRef, useEffect } from "react";
import { X, Users, UserPlus, Upload, ArrowLeft, ArrowRight, Sparkles, PartyPopper, Rocket } from "lucide-react";

export interface ServerWizardData {
  purpose: "community" | "friends" | null;
  name: string;
  iconFile: File | null;
  iconPreview: string | null;
  topics: string[];
}

interface ServerWizardProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateServer: (data: ServerWizardData) => Promise<number | null>; // Renvoie l'identifiant du serveur
  onGoToServer: (serverId: number) => void;
}

const TOTAL_STEPS = 4;

const TOPIC_OPTIONS = [
  "Jeux",
  "Musique",
  "Art",
  "Tech",
  "Education",
  "Sport",
  "Cinema",
  "Livres",
];

export function ServerWizard({ isOpen, onClose, onCreateServer, onGoToServer }: ServerWizardProps) {
  const [step, setStep] = useState(1);
  const [data, setData] = useState<ServerWizardData>({
    purpose: null,
    name: "",
    iconFile: null,
    iconPreview: null,
    topics: [],
  });
  const [createdServerId, setCreatedServerId] = useState<number | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Reinitialise l'etat a l'ouverture de la fenetre
  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setCreatedServerId(null);
      setIsCreating(false);
      setData({
        purpose: null,
        name: "",
        iconFile: null,
        iconPreview: null,
        topics: [],
      });
    }
  }, [isOpen]);

  // Gestion de la touche Echap
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

  const handlePurposeSelect = (purpose: "community" | "friends") => {
    setData((d) => ({ ...d, purpose }));
    goNext();
  };

  const handleIconUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setData((d) => ({
          ...d,
          iconFile: file,
          iconPreview: reader.result as string,
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setData((d) => ({ ...d, name: e.target.value }));
  };

  const toggleTopic = (topic: string) => {
    setData((d) => ({
      ...d,
      topics: d.topics.includes(topic)
        ? d.topics.filter((t) => t !== topic)
        : [...d.topics, topic],
    }));
  };

  const handleCreate = async () => {
    setIsCreating(true);
    try {
      const serverId = await onCreateServer(data);
      console.log("ServerWizard: received serverId from onCreateServer:", serverId);
      setCreatedServerId(serverId);
      setStep(4);
    } catch (error) {
      console.error("Echec de creation du serveur :", error);
    } finally {
      setIsCreating(false);
    }
  };

  const handleGoToServer = () => {
    console.log("ServerWizard: handleGoToServer, createdServerId:", createdServerId);
    if (createdServerId) {
      onGoToServer(createdServerId);
    }
    onClose();
  };

  const canProceedStep2 = data.name.trim().length >= 2;

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

          {/* Contenu des etapes avec animations */}
        <div className="relative min-h-[400px] overflow-hidden">
          {/* Etape 1 : choix du type de serveur */}
          <div
            className={`absolute inset-0 p-6 pt-12 flex flex-col transition-all duration-300 ease-out ${
              step === 1
                ? "opacity-100 translate-x-0 scale-100"
                : step > 1
                ? "opacity-0 -translate-x-full scale-95 pointer-events-none"
                : "opacity-0 translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center mb-8">
              <h2 className="text-2xl font-bold text-white mb-2">Créer un serveur</h2>
              <p className="text-gray-400 text-sm">
                Votre serveur est l'endroit où vous et vos amis traînez ensemble. Créez le vôtre et commencez à discuter.
              </p>
            </div>

            <div className="flex-1 flex flex-col gap-3">
              <button
                onClick={() => handlePurposeSelect("community")}
                className="group flex items-center gap-4 p-4 bg-[#2b2d31] hover:bg-[#404249] rounded-lg border border-transparent hover:border-gray-600 transition-all duration-200"
              >
                <div className="w-12 h-12 rounded-full bg-[#5865F2]/20 flex items-center justify-center group-hover:bg-[#5865F2]/30 transition-colors">
                  <Users className="w-6 h-6 text-[#5865F2]" />
                </div>
                <div className="flex-1 text-left">
                  <p className="text-white font-medium">Pour un club ou une communauté</p>
                  <p className="text-gray-400 text-sm">Créez un espace pour un grand groupe avec des rôles et des permissions</p>
                </div>
                <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-white transition-colors" />
              </button>

              <button
                onClick={() => handlePurposeSelect("friends")}
                className="group flex items-center gap-4 p-4 bg-[#2b2d31] hover:bg-[#404249] rounded-lg border border-transparent hover:border-gray-600 transition-all duration-200"
              >
                <div className="w-12 h-12 rounded-full bg-[#3ba55d]/20 flex items-center justify-center group-hover:bg-[#3ba55d]/30 transition-colors">
                  <UserPlus className="w-6 h-6 text-[#3ba55d]" />
                </div>
                <div className="flex-1 text-left">
                  <p className="text-white font-medium">Pour moi et mes amis</p>
                  <p className="text-gray-400 text-sm">Un petit espace privé pour discuter avec vos proches</p>
                </div>
                <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-white transition-colors" />
              </button>
            </div>
          </div>

          {/* Etape 2 : personnalisation du serveur */}
          <div
            className={`absolute inset-0 p-6 pt-10 pb-4 flex flex-col transition-all duration-300 ease-out ${
              step === 2
                ? "opacity-100 translate-x-0 scale-100"
                : step > 2
                ? "opacity-0 -translate-x-full scale-95 pointer-events-none"
                : "opacity-0 translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center mb-4">
              <h2 className="text-xl font-bold text-white mb-1">Personnalisez votre serveur</h2>
              <p className="text-gray-400 text-sm">
                Donnez une personnalité à votre serveur avec un nom et une icône.
              </p>
            </div>

            {/* Ajout de l'icone */}
            <div className="flex justify-center mb-4">
              <button
                onClick={() => fileInputRef.current?.click()}
                className="relative group w-16 h-16 rounded-full bg-[#2b2d31] border-2 border-dashed border-gray-500 hover:border-[#5865F2] flex items-center justify-center transition-all duration-200 overflow-hidden"
              >
                {data.iconPreview ? (
                  <img
                    src={data.iconPreview}
                    alt="Apercu de l'icone du serveur"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="flex flex-col items-center text-gray-400 group-hover:text-[#5865F2] transition-colors">
                    <Upload className="w-5 h-5 mb-0.5" />
                    <span className="text-[10px] font-medium">AJOUTER</span>
                  </div>
                )}
                {data.iconPreview && (
                  <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity">
                    <Upload className="w-5 h-5 text-white" />
                  </div>
                )}
              </button>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleIconUpload}
                className="hidden"
              />
            </div>

            {/* Saisie du nom du serveur */}
            <div className="mb-4 flex-1">
              <label className="block text-xs font-semibold text-gray-300 uppercase mb-2">
                Nom du serveur
              </label>
              <input
                type="text"
                value={data.name}
                onChange={handleNameChange}
                placeholder={data.purpose === "community" ? "Ma communauté" : "Le serveur de mes amis"}
                className="w-full px-4 py-2.5 bg-[#1e1f22] border border-transparent focus:border-[#5865F2] rounded-md text-white placeholder-gray-500 outline-none transition-colors"
              />
              <p className="text-xs text-gray-500 mt-1.5">
                En créant un serveur, vous acceptez les règles de la communauté Eris.
              </p>
            </div>

            {/* Boutons de navigation */}
            <div className="flex gap-3">
              <button
                onClick={goBack}
                className="flex-1 px-4 py-2.5 text-gray-300 hover:text-white transition-colors"
              >
                Retour
              </button>
              <button
                onClick={goNext}
                disabled={!canProceedStep2}
                className="flex-1 px-4 py-2.5 bg-[#5865F2] hover:bg-[#4752C4] disabled:bg-[#5865F2]/50 disabled:cursor-not-allowed text-white font-medium rounded-md transition-colors"
              >
                Suivant
              </button>
            </div>
          </div>

          {/* Etape 3 : themes (optionnel) */}
          <div
            className={`absolute inset-0 p-6 pt-8 pb-4 flex flex-col transition-all duration-300 ease-out ${
              step === 3
                ? "opacity-100 translate-x-0 scale-100"
                : step > 3
                ? "opacity-0 -translate-x-full scale-95 pointer-events-none"
                : "opacity-0 translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center mb-3">
              <div className="inline-flex items-center justify-center w-10 h-10 rounded-full bg-[#5865F2]/20 mb-2">
                <Sparkles className="w-5 h-5 text-[#5865F2]" />
              </div>
              <h2 className="text-xl font-bold text-white mb-1">Thème du serveur</h2>
              <p className="text-gray-400 text-xs">
                Choisissez quelques sujets (optionnel)
              </p>
            </div>

            {/* Grille des themes */}
            <div className="flex-1 overflow-y-auto min-h-0">
              <div className="flex flex-wrap gap-2 justify-center">
                {TOPIC_OPTIONS.map((topic) => (
                  <button
                    key={topic}
                    onClick={() => toggleTopic(topic)}
                    className={`px-3 py-1.5 rounded-full text-sm font-medium transition-all duration-200 ${
                      data.topics.includes(topic)
                        ? "bg-[#5865F2] text-white"
                        : "bg-[#2b2d31] text-gray-300 hover:bg-[#404249]"
                    }`}
                  >
                    {topic}
                  </button>
                ))}
              </div>
            </div>

            {/* Boutons de navigation */}
            <div className="flex gap-3 mt-4">
              <button
                onClick={goBack}
                className="flex items-center justify-center gap-2 px-4 py-2.5 text-gray-300 hover:text-white transition-colors"
              >
                <ArrowLeft className="w-4 h-4" />
                Retour
              </button>
              <button
                onClick={handleCreate}
                disabled={isCreating}
                className="flex-1 px-4 py-2.5 bg-[#5865F2] hover:bg-[#4752C4] disabled:bg-[#5865F2]/50 disabled:cursor-not-allowed text-white font-medium rounded-md transition-colors"
              >
                {isCreating ? "Création..." : "Créer mon serveur"}
              </button>
            </div>

            {/* Option pour ignorer */}
            <button
              onClick={handleCreate}
              disabled={isCreating}
              className="mt-2 text-sm text-gray-400 hover:text-gray-300 transition-colors disabled:opacity-50"
            >
              {isCreating ? "Création..." : "Passer cette étape"}
            </button>
          </div>

          {/* Etape 4 : succes / bienvenue */}
          <div
            className={`absolute inset-0 p-6 pt-8 pb-4 flex flex-col items-center justify-center transition-all duration-300 ease-out ${
              step === 4
                ? "opacity-100 translate-x-0 scale-100"
                : "opacity-0 translate-x-full scale-95 pointer-events-none"
            }`}
          >
            <div className="text-center">
              {/* Icone de celebration */}
              <div className="relative inline-flex items-center justify-center mb-4">
                <div className="w-20 h-20 rounded-full bg-gradient-to-br from-[#5865F2] to-[#3ba55d] flex items-center justify-center animate-pulse">
                  <PartyPopper className="w-10 h-10 text-white" />
                </div>
                {/* Decorations */}
                <Sparkles className="absolute -top-2 -right-2 w-6 h-6 text-yellow-400 animate-bounce" />
                <Sparkles className="absolute -bottom-1 -left-3 w-5 h-5 text-yellow-400 animate-bounce" style={{ animationDelay: "0.2s" }} />
              </div>

              <h2 className="text-2xl font-bold text-white mb-2">
                Ton serveur est prêt !
              </h2>
              <p className="text-gray-400 text-sm mb-2">
                Bienvenue sur <span className="text-white font-semibold">{data.name}</span>
              </p>
              <p className="text-gray-500 text-xs mb-6">
                Invite tes amis et commence à discuter !
              </p>

              {/* Bouton d'acces au serveur */}
              <button
                onClick={handleGoToServer}
                className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-[#5865F2] hover:bg-[#4752C4] text-white font-medium rounded-md transition-all duration-200 hover:scale-[1.02]"
              >
                <Rocket className="w-5 h-5" />
                Aller à mon serveur
              </button>
            </div>
          </div>
        </div>

        {/* Points de progression */}
        <div className="flex justify-center gap-2 pb-6">
          {Array.from({ length: TOTAL_STEPS }).map((_, i) => (
            <div
              key={i}
              onClick={() => {
                if (i + 1 < step) {
                  setStep(i + 1);
                }
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
      </div>
    </div>
  );
}
