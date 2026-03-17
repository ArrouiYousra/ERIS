import { useState } from "react";
import { useChannels } from "../hooks/useChannels";
import {
  useServers,
  useCreateServer,
  useDeleteServer,
  useLeaveServer,
  useServerMembers,
  useServerRoles
} from "../hooks/useServers";
import { useAuth } from "../hooks/useAuth";
import { useQueryClient } from "@tanstack/react-query";
import type { Channel } from "../api/channelsApi";
import type { Server } from "../api/serversApi";
import { ServerBar, RightPanel, UserBar } from "../components/friends";
import { ChannelList } from "../components/ChannelList";
import { MessageList } from "../components/MessageList";
import { ServerGate } from "../components/ServerGate";
import {
  ServerWizard,
  type ServerWizardData,
} from "../components/ServerWizard";
import "../styles/chat.css";
import "../styles/serverWizard.css";
import { joinWithInvitation } from "../api/invitationApi";
import { usePresence } from "../hooks/usePresence";
import type { ServerMember } from "../api/serverMembersApi";
import { useServerSocket } from '../hooks/useServerSocket';
import { usePresenceSocket } from '../hooks/usePresenceSocket';

import { MemberContextMenu } from "../components/MemberContextMenu";

export function ChatLayout() {
  const queryClient = useQueryClient();
  const [selectedServerId, setSelectedServerId] = useState<number | null>(null);
  const [selectedChannelId, setSelectedChannelId] = useState<number | null>(
    null,
  );

  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false);
  const [serverModalOpen, setServerModalOpen] = useState(false);

  const { user } = useAuth();
  const { data: servers = [] } = useServers();
  const { data: channels = [] } = useChannels(selectedServerId);
  const { data: roles = [] } = useServerRoles(selectedServerId);
  const createServer = useCreateServer();
  const deleteServer = useDeleteServer();
  const leaveServer = useLeaveServer();

  useServerSocket();
  usePresenceSocket(selectedServerId);

  const serverIds = servers.map((s: Server) => s.id);
  const serverNames = Object.fromEntries(
    servers.map((s: Server) => [s.id, s.name]),
  );

  const isDMMode = selectedServerId === null;

  // Check if current user is the owner of the selected server
  const currentServer = servers.find((s: Server) => s.id === selectedServerId);
  const isServerOwner = !!(
    currentServer &&
    user &&
    currentServer.ownerId === user.id
  );
  const selectedChannel = channels.find(
    (channel: Channel) => channel.id === selectedChannelId,
  );

  const handleDeleteServer = async () => {
    if (!selectedServerId) return;
    await deleteServer.mutateAsync(selectedServerId);
    setSelectedServerId(null);
    setSelectedChannelId(null);
  };

  const handleSelectServer = (id: number | null) => {
    setSelectedServerId(id);
    setSelectedChannelId(null);
  };

  // Handler for ServerWizard modal (full data object)
  const handleCreateServerFromWizard = async (
    data: ServerWizardData,
  ): Promise<number | null> => {
    const result = await createServer.mutateAsync({ name: data.name });
    return result?.id ?? null;
  };

  // Handler for navigating to a newly created server
  const handleGoToServer = (serverId: number) => {
    setSelectedServerId(serverId);
    setSelectedChannelId(null);
  };

  // Handler for ServerGate (simple string name - legacy)
  const handleCreateServer = async (name: string) => {
    const result = await createServer.mutateAsync({ name });
    if (result?.id) setSelectedServerId(result.id);
  };

  const handleJoinServer = async (inviteLink: string) => {
    await joinWithInvitation(inviteLink);
    queryClient.invalidateQueries({ queryKey: ["servers"] });
    setServerModalOpen(false);
  };

  const handleLeaveServer = async () => {
    if (!selectedServerId) return;
    try {
      await leaveServer.mutateAsync(selectedServerId);
      setSelectedServerId(null);
      setSelectedChannelId(null);
    } catch (error) {
      console.error("Impossible de quitter le serveur :", error);
    }
  };

  return (
    <div className="chat-layout-root">
      {/* Zone 1: Server bar — 72px */}
      <ServerBar
        selectedServerId={selectedServerId}
        onSelectServer={handleSelectServer}
        serverIds={serverIds}
        serverNames={serverNames}
        onAddServer={() => setServerModalOpen(true)}
      />

      {isDMMode ? (
        <div className="chat-dm-wrapper">
          {/* Sidebar DM avec UserBar en bas */}
          <div className="chat-dm-sidebar shrink-0 h-full hidden md:flex flex-col bg-[#2b2d31]">
            <div className="flex-1 overflow-y-auto" />
            <UserBar />
          </div>
          {/* Zone 3: Main content — onglets + contenu (prend le reste) */}
          <ServerGate
            onCreateServer={handleCreateServer}
            onJoinServer={handleJoinServer}
          />
          {/* Zone 4: Right panel — repliable sur tablette */}
          <RightPanel
            collapsed={rightPanelCollapsed}
            onToggle={() => setRightPanelCollapsed((c) => !c)}
          />
          <div className="chat-dm-mobile-userbar md:hidden">
            <UserBar />
          </div>
        </div>
      ) : (
        <div
          className={`chat-server-mode ${selectedChannelId === null ? "chat-server-mode--mobile-list-only" : ""} ${selectedChannelId !== null ? "chat-server-mode--mobile-channel-open" : ""}`}
        >
          {/* Mode serveur : sidebar canaux + zone messages */}
          <div className="chat-sidebar chat-sidebar--channels shrink-0">
            <ChannelList
              serverId={selectedServerId}
              channels={channels}
              onSelectChannel={(channelId) => {
                setSelectedChannelId(channelId);
              }}
              selectedChannelId={selectedChannelId}
              serverName={
                selectedServerId ? serverNames[selectedServerId] : "Serveur"
              }
              isOwner={isServerOwner}
              onDeleteServer={handleDeleteServer}
              onLeaveServer={handleLeaveServer}
            />
          </div>
          <div className="chat-main-shell">
            <div className="chat-main-messages">
              <MessageList
                channelId={selectedChannelId}
                serverId={selectedServerId}
                channelName={selectedChannel?.name}
                channelTopic={selectedChannel?.topic}
                isPrivate={selectedChannel?.isPrivate}
                serverName={
                  selectedServerId ? serverNames[selectedServerId] : "Serveur"
                }
                onToggleSidebar={() => setSelectedChannelId(null)}
                onLeaveServer={handleLeaveServer}
              />
            </div>
            {showMemberList && (() => {
              const grouped = members.reduce((acc: Record<string, ServerMember[]>, member: ServerMember) => {
                const roleName = member.roleName ?? "Membres";
                if (!acc[roleName]) acc[roleName] = [];
                acc[roleName].push(member);
                return acc;
              }, {});

              return (
                <div className="w-60 h-full bg-[#2b2d31] border-l border-black/20 overflow-y-auto shrink-0">
                  <div className="p-4 space-y-3">
                    {Object.entries(grouped)
                      .sort(([nameA], [nameB]) => {
                        const roleA = roles.find((r) => r.name === nameA)?.id ?? 999;
                        const roleB = roles.find((r) => r.name === nameB)?.id ?? 999;
                        return roleB - roleA;
                      })
                      .map(([roleName, roleMembers]) => (
                        <div key={roleName}>
                          <h3 className="text-xs font-semibold text-gray-400 uppercase mb-2">
                            {roleName} — {roleMembers.length}
                          </h3>
                          <div className="space-y-0.5">
                            {(roleMembers as ServerMember[]).map((member: ServerMember) => {
                              const isOnline = onlineUserIds.has(member.userId);
                              return (
                                <div
                                  key={member.userId}
                                  className={`flex items-center gap-3 p-1.5 rounded hover:bg-white/5 cursor-pointer ${!isOnline ? "opacity-40" : ""}`}
                                  onContextMenu={(e) => {
                                    e.preventDefault();
                                    setCtxMenu({ member, x: e.clientX, y: e.clientY });
                                  }}
                                >
                                  <div className="relative">
                                    <div className="w-8 h-8 rounded-full bg-[#5865F2] flex items-center justify-center text-white text-sm font-medium">
                                      {(member.nickname || member.username || "U").charAt(0).toUpperCase()}
                                    </div>
                                    <div className={`absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-[#2b2d31] ${isOnline ? "bg-[#23a559]" : "bg-[#80848e]"}`} />
                                  </div>
                                  <span className="text-gray-300 text-sm">
                                    {member.nickname || member.username || `User ${member.userId}`}
                                  </span>
                                </div>
                              );
                            })}
                          </div>
                        </div>
                      ))}
                  </div>
                </div>
              );
            })()}
          </div>
        </div>
      )}

      {/* Server creation wizard modal */}
      <ServerWizard
        isOpen={serverModalOpen}
        onClose={() => setServerModalOpen(false)}
        onCreateServer={handleCreateServerFromWizard}
        onGoToServer={handleGoToServer}
      />
    </div>
  );
}
