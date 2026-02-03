import { Mic, Headphones, Settings } from "lucide-react";
import { Avatar } from "./Avatar";

const MOCK_USER = {
  displayName: "Moi",
  username: "moi",
  status: "online" as const,
  avatarColor: "#5865F2",
};

export function UserBar() {
  return (
    <div className="flex items-center gap-3 px-3 py-2 bg-[#1a1d24] border-t border-white/[0.06] min-h-[52px]">
      <Avatar initial={MOCK_USER.displayName[0]} color={MOCK_USER.avatarColor} size="sm" />
      <div className="flex-1 min-w-0">
        <div className="text-sm font-medium text-[#f2f3f5] truncate">{MOCK_USER.displayName}</div>
        <div className="text-xs text-[#b5bac1] truncate">{MOCK_USER.username}</div>
      </div>
      <div className="flex items-center gap-1 shrink-0">
        <button
          type="button"
          className="p-1.5 rounded-md text-[#b5bac1] hover:bg-white/[0.06] hover:text-[#f2f3f5] transition-colors"
          title="Micro"
          aria-label="Micro"
        >
          <Mic className="w-4 h-4" />
        </button>
        <button
          type="button"
          className="p-1.5 rounded-md text-[#b5bac1] hover:bg-white/[0.06] hover:text-[#f2f3f5] transition-colors"
          title="Casque"
          aria-label="Casque"
        >
          <Headphones className="w-4 h-4" />
        </button>
        <button
          type="button"
          className="p-1.5 rounded-md text-[#b5bac1] hover:bg-white/[0.06] hover:text-[#f2f3f5] transition-colors"
          title="Paramètres"
          aria-label="Paramètres"
        >
          <Settings className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
