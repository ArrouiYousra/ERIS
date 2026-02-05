import { PanelRightOpen } from "lucide-react";
import { useServerMember } from "../../hooks/useServers";

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
  const { data: serverMembers = [], isLoading } = useServerMember(serverId);

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
        ) : serverMembers.length === 0 ? (
          <p className="text-sm text-[#b5bac1]">Aucun membre trouvé.</p>
        ) : (
          serverMembers.map((member: { id: number; username: string }) => (
            <div
              key={member.id}
              className="flex items-center gap-2 p-2 rounded-lg hover:bg-[#202432] cursor-pointer"
            >
              <div className="w-8 h-8 bg-[#5865F2] rounded-full flex items-center justify-center text-white text-sm">
                {member.username.charAt(0).toUpperCase()}
              </div>
              <span className="text-sm text-[#f2f3f5]">{member.username}</span>
            </div>
          ))
        )}
      </div>
    </aside>
  );
}
