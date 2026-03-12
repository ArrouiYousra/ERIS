import { PanelRightOpen } from "lucide-react";
import { useServerMembers } from "../../hooks/useServers";
import { usePresence } from "../../hooks/usePresence";

interface LeftPanelProps {
  serverId: number | null;
  collapsed?: boolean;
  onToggle?: () => void;
}

export function LeftPanel({
  serverId,
  collapsed = false,
  onToggle,
}: LeftPanelProps) {
  const { data: serverMembers = [], isLoading } = useServerMembers(serverId);
  const { onlineUserIds } = usePresence(serverId);

  if (collapsed) {
    return (
      <aside
        className="h-screen w-12 shrink-0 flex flex-col items-center justify-center py-4 bg-[#1a1d24] border-r border-white/[0.06] cursor-pointer hover:bg-[#202432] transition-colors"
        onClick={onToggle}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => e.key === "Enter" && onToggle?.()}
        aria-label="Ouvrir le panneau gauche Membres du serveur"
      >
        <PanelRightOpen
          className="w-5 h-5 text-[#b5bac1] rotate-180"
          aria-hidden
        />
      </aside>
    );
  }

  // Séparer online / offline
  const online = serverMembers.filter((m: any) => onlineUserIds.has(m.userId));
  const offline = serverMembers.filter((m: any) => !onlineUserIds.has(m.userId));

  return (
    <aside
      className="h-screen w-[240px] min-w-[240px] shrink-0 flex flex-col bg-[#1a1d24] border-r border-white/[0.06] lg:w-[220px] lg:min-w-[220px]"
      aria-label="Panneau gauche Membres du serveur"
    >
      <div className="shrink-0 px-4 py-3 border-b border-white/[0.06]">
        <h2 className="text-base font-semibold text-[#f2f3f5]">Membres</h2>
      </div>

      <div className="flex-1 min-h-0 overflow-y-auto p-4 space-y-2">
        {isLoading ? (
          <p className="text-sm text-[#b5bac1]">Chargement des membres...</p>
        ) : (
          <>
            {online.length > 0 && (
              <>
                <p className="text-xs font-semibold text-[#b5bac1] uppercase">
                  En ligne — {online.length}
                </p>
                {online.map((member: any) => (
                  <MemberRow key={member.userId} member={member} isOnline={true} />
                ))}
              </>
            )}
            {offline.length > 0 && (
              <>
                <p className="text-xs font-semibold text-[#b5bac1] uppercase mt-4">
                  Hors ligne — {offline.length}
                </p>
                {offline.map((member: any) => (
                  <MemberRow key={member.userId} member={member} isOnline={false} />
                ))}
              </>
            )}
            {online.length === 0 && offline.length === 0 && (
              <p className="text-sm text-[#b5bac1]">Aucun membre trouvé.</p>
            )}
          </>
        )}
      </div>
    </aside>
  );
}

function MemberRow({ member, isOnline }: { member: any; isOnline: boolean }) {
  return (
    <div
      className={`flex items-center gap-2 p-2 rounded-lg hover:bg-[#202432] cursor-pointer ${!isOnline ? "opacity-40" : ""}`}
    >
      <div className="relative">
        <div className="w-8 h-8 bg-[#5865F2] rounded-full flex items-center justify-center text-white text-sm">
          {(member.nickname || member.username || "?").charAt(0).toUpperCase()}
        </div>
        <span
          className={`absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 rounded-full border-2 border-[#1a1d24] ${isOnline ? "bg-[#23a559]" : "bg-[#80848e]"}`}
        />
      </div>
      <span className="text-sm text-[#f2f3f5]">
        {member.nickname || member.username || `User ${member.userId}`}
      </span>
    </div>
  );
}