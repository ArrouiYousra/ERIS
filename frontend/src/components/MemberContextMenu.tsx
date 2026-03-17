import { useEffect, useMemo, useRef, useState } from "react";
import { useServerRoles, useUpdateMemberRole } from "../hooks/useServers";
import type { ServerMember } from "../api/serverMembersApi";

interface MemberContextMenuProps {
  member: ServerMember;
  serverId: number;
  isOwner: boolean; // is the current user the server owner?
  currentUserId: number;
  position: { x: number; y: number };
  onClose: () => void;
  onKick?: (member: ServerMember) => void;
  onBan?: (member: ServerMember) => void;
}

export function MemberContextMenu({
  member,
  serverId,
  isOwner,
  currentUserId,
  position,
  onClose,
  onKick,
  onBan,
}: MemberContextMenuProps) {
  const menuRef = useRef<HTMLDivElement>(null);
  const [submenuOpen, setSubmenuOpen] = useState(false);

  const { data: roles = [] } = useServerRoles(serverId);
  const updateRole = useUpdateMemberRole();

  const isSelf = member.userId === currentUserId;
  const canManage = isOwner && !isSelf;
 
  // Keep the menu visible with a best-effort clamp without effect state updates.
  const adjustedPos = useMemo(() => {
    const MENU_WIDTH = 224; // w-56
    const MENU_HEIGHT_ESTIMATE = 380;
    const PADDING = 8;

    const vw = window.innerWidth;
    const vh = window.innerHeight;
    let { x, y } = position;
    if (x + MENU_WIDTH > vw) x = vw - MENU_WIDTH - PADDING;
    if (y + MENU_HEIGHT_ESTIMATE > vh) y = vh - MENU_HEIGHT_ESTIMATE - PADDING;
    if (x < PADDING) x = PADDING;
    if (y < PADDING) y = PADDING;
    return { x, y };
  }, [position]);

  // Close on outside click or Escape
  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        onClose();
      }
    };
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    document.addEventListener("mousedown", handleClick);
    document.addEventListener("keydown", handleKey);
    return () => {
      document.removeEventListener("mousedown", handleClick);
      document.removeEventListener("keydown", handleKey);
    };
  }, [onClose]);

  const handleRoleChange = async (roleId: number) => {
    await updateRole.mutateAsync({ serverId, memberId: member.userId, payload: { roleId } });
    setSubmenuOpen(false);
    onClose();
  };

  const displayName = member.nickname || member.username || `User ${member.userId}`;

  return (
    <div
      ref={menuRef}
      style={{ left: adjustedPos.x, top: adjustedPos.y }}
      className="fixed z-50 w-56 select-none"
      onContextMenu={(e) => e.preventDefault()}
    >
      <div className="rounded-md overflow-visible shadow-2xl border border-white/5"
        style={{ background: "#111214" }}>

        {/* Actions */}
        <div className="py-1">

          {/* Owner-only actions */}
          {canManage && (
            <>
              <div className="my-1 border-t border-white/5" />

              {/* Change role submenu */}
              {roles.length > 0 && (
                <div className="relative">
                  <MenuButton
                    icon={
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                      </svg>
                    }
                    label="Modifier le rôle"
                    hasSubmenu
                    onClick={() => setSubmenuOpen((v) => !v)}
                  />
                  {submenuOpen && (
                    <div
                      className="absolute right-full top-0 w-44 mr-1 rounded-md shadow-2xl border border-white/5 py-1"
                      style={{ background: "#111214" }}
                    >
                      {roles.map((role: { id: number; name: string; color?: string }) => (
                        <button
                          key={role.id}
                          onClick={() => handleRoleChange(role.id)}
                          className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-300 hover:bg-white/5 hover:text-white transition-colors"
                        >
                          <span
                            className="w-2.5 h-2.5 rounded-full shrink-0"
                            style={{ background: role.color || "#5865F2" }}
                          />
                          {role.name}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {/* Kick */}
              {onKick && (
                <MenuButton
                  icon={
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                    </svg>
                  }
                  label={`Expulser ${displayName}`}
                  danger
                  onClick={() => {
                    onKick(member);
                    onClose();
                  }}
                />
              )}

              {/* Ban */}
              {onBan && (
                <MenuButton
                  icon={
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                    </svg>
                  }
                  label={`Bannir ${displayName}`}
                  danger
                  onClick={() => {
                    onBan(member);
                    onClose();
                  }}
                />
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

/* ─── Sub-component ─── */

interface MenuButtonProps {
  icon: React.ReactNode;
  label: string;
  danger?: boolean;
  hasSubmenu?: boolean;
  onClick: () => void;
}

function MenuButton({ icon, label, danger, hasSubmenu, onClick }: MenuButtonProps) {
  return (
    <button
      onClick={onClick}
      className={`w-full flex items-center justify-between gap-2 px-2 py-1.5 mx-1 rounded text-sm transition-colors
        ${danger
          ? "text-[#f23f43] hover:bg-[#f23f43] hover:text-white"
          : "text-gray-300 hover:bg-[#5865F2] hover:text-white"
        }`}
      style={{ width: "calc(100% - 8px)" }}
    >
      <span className="flex items-center gap-2">
        {icon}
        {label}
      </span>
      {hasSubmenu && (
        <svg className="w-3 h-3 opacity-60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
      )}
    </button>
  );
}