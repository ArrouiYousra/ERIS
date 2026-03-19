import { useMemo, useState } from "react";
import { Plus } from "lucide-react";
import { SearchInput } from "./SearchInput";
import { UserBar } from "./UserBar";
import { MOCK_DMS } from "../../data/mockFriends";

interface MainSidebarProps {
  selectedDMId: string | null;
  onSelectDM: (id: string | null) => void;
  onAddDM?: () => void;
}

export function MainSidebar({ onAddDM }: MainSidebarProps) {
  const [search, setSearch] = useState("");

  const filteredDMs = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return MOCK_DMS;
    return MOCK_DMS.filter(
      (dm) =>
        dm.friend.displayName.toLowerCase().includes(q) ||
        dm.friend.username.toLowerCase().includes(q),
    );
  }, [search]);

  return (
    <aside
      className="h-screen w-[280px] min-w-[280px] max-w-[320px] shrink-0 flex flex-col bg-[#1a1d24] border-r border-white/[0.06] md:w-[300px] md:min-w-[300px]"
      aria-label="Sidebar principale"
    >
      {/* Recherche */}
      <div className="p-3 border-b border-white/[0.06] shrink-0">
        <SearchInput
          value={search}
          onChange={setSearch}
          placeholder="Recherche ou lance une conversation"
          aria-label="Rechercher ou démarrer une conversation"
        />
      </div>

      {/* Section Amis — item cliquable */}
      <button
        type="button"
        className="flex items-center gap-2 px-3 py-2.5 text-left text-sm font-medium text-[#b5bac1] hover:bg-white/[0.05] hover:text-[#f2f3f5] transition-colors border-b border-white/[0.06]"
      >
        Amis
      </button>

      {/* Section Messages privés + bouton + */}
      <div className="flex items-center justify-between px-3 py-2 border-b border-white/[0.06] shrink-0">
        <span className="text-xs font-semibold uppercase tracking-wide text-[#b5bac1]">
          Message privés
        </span>
        <button
          type="button"
          onClick={onAddDM}
          className="p-1 rounded text-[#b5bac1] hover:bg-white/[0.06] hover:text-[#f2f3f5] transition-colors"
          title="Nouvelle conversation"
          aria-label="Nouvelle conversation"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div>

      {/* Liste des DMs — filtre local (useMemo) */}
      <div className="flex-1 min-h-0 overflow-y-auto py-2">
        {filteredDMs.length === 0 ? (
          <div className="px-4 py-6 text-center text-sm text-[#949ba4]">
            {search.trim()
              ? "Aucune conversation ne correspond à la recherche."
              : "Aucun message privé."}
          </div>
        ) : (
          <div className="px-2 space-y-0.5"></div>
        )}
      </div>

      {/* Mini user bar en bas */}
      <UserBar />
    </aside>
  );
}
