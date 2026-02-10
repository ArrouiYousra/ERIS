import { LogOut } from "lucide-react";
import { Avatar } from "./Avatar";
import { useAuth } from "../../hooks/useAuth";

export function UserBar() {
  const { user, logout } = useAuth();

  const displayName = user?.displayName || user?.username || "Utilisateur";
  const username = user?.username || "user";

  return (
    <div className="flex items-center gap-2 px-2 py-2 bg-[#232428] border-t border-white/[0.06] min-h-[52px]">
      <Avatar initial={displayName[0]} color="#5865F2" size="sm" />
      <div className="flex-1 min-w-0">
        <div className="text-sm font-medium text-[#f2f3f5] truncate leading-tight">{displayName}</div>
        <div className="text-[11px] text-[#b5bac1] truncate leading-tight">{username}</div>
      </div>
      <button
        type="button"
        onClick={logout}
        className="p-1.5 rounded-md text-[#b5bac1] hover:bg-white/[0.08] hover:text-red-400 transition-colors shrink-0"
        title="Se déconnecter"
        aria-label="Se déconnecter"
      >
        <LogOut className="w-4 h-4" />
      </button>
    </div>
  );
}
