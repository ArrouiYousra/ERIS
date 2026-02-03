import { useMemo } from "react";
import { Avatar } from "./Avatar";
import { MOCK_FRIENDS } from "../../data/mockFriends";
import type { MainContentTab } from "../../types/friends";

const statusDot: Record<string, string> = {
  online: "bg-[#23a559]",
  offline: "bg-[#80848e]",
  idle: "bg-[#faa61a]",
};

interface FriendsListContentProps {
  activeTab: MainContentTab;
}

export function FriendsListContent({ activeTab }: FriendsListContentProps) {
  const filteredFriends = useMemo(() => {
    if (activeTab === "ONLINE") {
      return MOCK_FRIENDS.filter((f) => f.status === "online");
    }
    if (activeTab === "ALL") return MOCK_FRIENDS;
    if (activeTab === "PENDING") return [];
    if (activeTab === "BLOCKED") return [];
    return MOCK_FRIENDS;
  }, [activeTab]);

  if (activeTab === "PENDING") {
    return (
      <div className="flex flex-col items-center justify-center flex-1 p-8 text-center">
        <p className="text-[#b5bac1] text-sm">Aucune demande en attente.</p>
      </div>
    );
  }
  if (activeTab === "BLOCKED") {
    return (
      <div className="flex flex-col items-center justify-center flex-1 p-8 text-center">
        <p className="text-[#b5bac1] text-sm">Aucun utilisateur bloqué.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto p-4">
      <div className="space-y-1">
        {filteredFriends.map((friend) => (
          <button
            key={friend.id}
            type="button"
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left text-[#b5bac1] hover:bg-white/[0.05] hover:text-[#f2f3f5] transition-colors"
          >
            <div className="relative shrink-0">
              <Avatar initial={friend.displayName[0]} color={friend.avatarColor} size="md" />
              {friend.status !== "offline" && (
                <span
                  className={`absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-[#202432] ${statusDot[friend.status]}`}
                  aria-hidden
                />
              )}
            </div>
            <div className="flex-1 min-w-0">
              <div className="font-medium text-[#f2f3f5]">{friend.displayName}</div>
              <div className="text-xs text-[#949ba4]">{friend.username}</div>
            </div>
            {friend.lastMessagePreview && (
              <span className="text-xs text-[#949ba4] truncate max-w-[120px]">
                {friend.lastMessagePreview}
              </span>
            )}
          </button>
        ))}
      </div>
    </div>
  );
}
