import type { DM } from "../../types/friends";
import { Avatar } from "./Avatar";

interface DMRowProps {
  dm: DM;
  selected?: boolean;
  onClick: () => void;
}

const statusDot: Record<string, string> = {
  online: "bg-[#23a559]",
  offline: "bg-[#80848e]",
  idle: "bg-[#faa61a]",
};

export function DMRow({ dm, selected, onClick }: DMRowProps) {
  const { friend, lastMessagePreview, unread } = dm;

  return (
    <button
      type="button"
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors hover:bg-white/[0.05] ${
        selected ? "bg-white/[0.08] text-[#f2f3f5]" : "text-[#b5bac1]"
      }`}
    >
      <div className="relative shrink-0">
        <Avatar initial={friend.displayName[0]} color={friend.avatarColor} size="sm" />
        {friend.status !== "offline" && (
          <span
            className={`absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 rounded-full border-2 border-[#2b2d31] ${statusDot[friend.status]}`}
            aria-hidden
          />
        )}
      </div>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-medium truncate">{friend.displayName}</div>
        <div className={`text-xs truncate ${selected ? "text-[#b5bac1]" : "text-[#949ba4]"}`}>
          {lastMessagePreview}
        </div>
      </div>
      {unread && (
        <span className="w-2 h-2 rounded-full bg-[#5865F2] shrink-0" aria-hidden />
      )}
    </button>
  );
}
