import { UserPlus } from "lucide-react";
import type { MainContentTab } from "../../types/friends";
import { AddScreen } from "./AddScreen";
import { FriendsListContent } from "./FriendsListContent";

interface MainContentProps {
  activeTab: MainContentTab;
  onTabChange: (tab: MainContentTab) => void;
}

const TABS: { key: MainContentTab; label: string }[] = [
  { key: "ADD", label: "Ajouter" },
  { key: "ONLINE", label: "En ligne" },
  { key: "ALL", label: "Tous" },
  { key: "PENDING", label: "En attente" },
  { key: "BLOCKED", label: "Bloqués" },
];

export function MainContent({ activeTab, onTabChange }: MainContentProps) {
  return (
    <main
      className="chat-main-content flex-1 flex flex-col min-w-0 bg-[#202432]"
      style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column", overflow: "hidden" }}
    >
      {/* Header : onglets horizontaux + bouton Ajouter */}
      <header className="shrink-0 flex items-center gap-1 px-4 py-3 border-b border-white/[0.06] overflow-x-auto">
        {TABS.map(({ key, label }) => (
          <button
            key={key}
            type="button"
            onClick={() => onTabChange(key)}
            className={`shrink-0 px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              activeTab === key
                ? "bg-[#5865F2] text-white"
                : "text-[#b5bac1] hover:bg-white/[0.06] hover:text-[#f2f3f5]"
            }`}
          >
            {label}
          </button>
        ))}
        <button
          type="button"
          onClick={() => onTabChange("ADD")}
          className="shrink-0 ml-auto flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-[#23a559] hover:bg-white/[0.06] transition-colors"
          title="Ajouter un ami"
        >
          <UserPlus className="w-4 h-4" />
          Ajouter
        </button>
      </header>

      {/* Contenu : par défaut AddScreen (Ajouter), sinon liste filtrée */}
      {activeTab === "ADD" ? (
        <AddScreen />
      ) : (
        <FriendsListContent activeTab={activeTab} />
      )}
    </main>
  );
}
