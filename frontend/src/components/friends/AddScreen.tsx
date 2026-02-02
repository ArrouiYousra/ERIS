import { useState } from "react";
import { UserPlus, Compass } from "lucide-react";

export function AddScreen() {
  const [username, setUsername] = useState("");

  const handleSendRequest = (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) return;
    // Mock: log ou state
    setUsername("");
  };

  return (
    <div className="flex flex-col items-center justify-center flex-1 p-8 text-center max-w-md mx-auto">
      <h2 className="text-2xl font-bold text-[#f2f3f5] mb-2">Ajouter</h2>
      <p className="text-[#b5bac1] text-sm leading-relaxed mb-6">
        Vous pouvez ajouter des amis avec leur pseudo Eris. Saisissez un pseudo ci-dessous pour
        envoyer une demande d&apos;ami.
      </p>

      <form onSubmit={handleSendRequest} className="w-full space-y-3 mb-8">
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Saisir un pseudo"
          className="w-full h-10 px-3 rounded-lg bg-[#1e1f22] border border-white/[0.06] text-[#f2f3f5] placeholder:text-[#4e5058] text-sm focus:outline-none focus:ring-2 focus:ring-[#5865F2] focus:border-transparent"
          aria-label="Pseudo à ajouter"
        />
        <button
          type="submit"
          disabled={!username.trim()}
          className="w-full h-10 rounded-lg bg-[#5865F2] text-white font-medium text-sm hover:bg-[#4752c4] disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
        >
          <UserPlus className="w-4 h-4" />
          Envoyer une demande d&apos;ami
        </button>
      </form>

      {/* Card "Autres endroits où se faire des amis" */}
      <div className="w-full p-4 rounded-xl bg-[#1a1d24] border border-white/[0.06] text-left">
        <h3 className="text-sm font-semibold text-[#f2f3f5] mb-2">
          Autres endroits où se faire des amis
        </h3>
        <p className="text-xs text-[#b5bac1] mb-3">
          Trouvez des communautés qui vous correspondent et discutez avec d&apos;autres personnes.
        </p>
        <button
          type="button"
          className="w-full h-9 rounded-lg bg-[#202432] text-[#f2f3f5] font-medium text-sm hover:bg-white/[0.06] transition-colors flex items-center justify-center gap-2"
        >
          <Compass className="w-4 h-4" />
          Explorer des serveurs
        </button>
      </div>
    </div>
  );
}
