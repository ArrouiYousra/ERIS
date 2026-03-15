import { Plus } from "lucide-react";
import erisIcon from "../../assets/eris_icone.png";

interface ServerBarProps {
  selectedServerId: number | null;
  onSelectServer: (id: number | null) => void;
  serverIds: number[];
  serverNames: Record<number, string>;
  onAddServer: () => void;
}

export function ServerBar({
  selectedServerId,
  onSelectServer,
  serverIds,
  serverNames,
  onAddServer,
}: ServerBarProps) {
  const isDMActive = selectedServerId === null;

  return (
    <aside
      className="relative z-40 h-full w-[72px] max-sm:w-14 shrink-0 flex flex-col items-center py-3 max-sm:py-2 bg-[#0f1115] border-r border-white/[0.06]"
      aria-label="Barre des serveurs"
    >
      {/* Messages privés — actif = barre à gauche */}
      <button
        type="button"
        onClick={() => onSelectServer(null)}
        className="relative flex items-center justify-center w-12 h-12 max-sm:w-10 max-sm:h-10 min-w-[40px] min-h-[40px] rounded-full bg-[#1a1d24] hover:rounded-xl hover:bg-[#5865F2] transition-all duration-200 group mb-2 shrink-0 cursor-pointer"
        title="Message privés"
        aria-pressed={isDMActive}
        aria-label="Message privés"
      >
        {isDMActive && (
          <span
            className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-white rounded-r pointer-events-none"
            aria-hidden
          />
        )}
        <span className="flex items-center justify-center w-6 h-6 max-w-[24px] max-h-[24px] shrink-0 overflow-hidden">
          <img
            src={erisIcon}
            alt=""
            className="w-full h-full object-contain object-center"
            width={24}
            height={24}
            draggable={false}
          />
        </span>
      </button>

      {/* Séparateur */}
      <div className="w-8 h-0.5 rounded-full bg-white/[0.06] my-1 shrink-0" />

      {/* Liste des serveurs — scroll si trop d'items */}
      <div className="flex-1 min-h-0 overflow-y-auto flex flex-col items-center gap-2 py-1">
        {serverIds.map((id) => {
          const isActive = selectedServerId === id;
          return (
            <button
              key={id}
              type="button"
              onClick={() => onSelectServer(id)}
              className="relative flex items-center justify-center w-12 h-12 max-sm:w-10 max-sm:h-10 rounded-full bg-[#1a1d24] hover:rounded-xl hover:bg-[#5865F2] transition-all duration-200 text-white font-semibold text-lg max-sm:text-base shrink-0"
              title={serverNames[id] ?? `Serveur ${id}`}
              aria-pressed={isActive}
            >
              {isActive && (
                <span
                  className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-white rounded-r pointer-events-none"
                  aria-hidden
                />
              )}
              {(serverNames[id] ?? "?").charAt(0).toUpperCase()}
            </button>
          );
        })}
      </div>

      {/* Bouton + */}
      <button
        type="button"
        onClick={onAddServer}
        className="flex items-center justify-center w-12 h-12 max-sm:w-10 max-sm:h-10 rounded-full bg-[#1a1d24] text-[#3ba55d] hover:rounded-xl hover:bg-[#3ba55d] hover:text-white transition-all duration-200 shrink-0 mt-1"
        title="Ajouter un serveur"
        aria-label="Ajouter un serveur"
      >
        <Plus className="w-6 h-6" strokeWidth={2.5} />
      </button>
    </aside>
  );
}
