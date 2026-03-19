import { useState } from "react";
// import { useChannels } from "../hooks/useChannels";
// import {
//   useServers,
//   useCreateServer,
//   useDeleteServer,
//   useLeaveServer,
//   useServerMembers,
//   useServerRoles,
// } from "../hooks/useServers";
// import { useAuth } from "../hooks/useAuth";
// import { useQueryClient } from "@tanstack/react-query";
// import type { Channel } from "../api/channelsApi";
// import type { Server } from "../api/serversApi";
import { RightPanel, UserBar, DMRow } from "../components/friends";
// import { ChannelList } from "../components/ChannelList";
// import { MessageList } from "../components/MessageList";
// import { ServerGate } from "../components/ServerGate";
// import {
//   ServerWizard,
//   type ServerWizardData,
// } from "../components/ServerWizard";
import "../styles/chat.css";
import "../styles/serverWizard.css";
// import { joinWithInvitation } from "../api/invitationApi";
// import { usePresence } from "../hooks/usePresence";
// import type { ServerMember } from "../api/serverMembersApi";
import { useServerSocket } from "../hooks/useServerSocket";
// import { usePresenceSocket } from "../hooks/usePresenceSocket";

// import { MemberContextMenu } from "../components/MemberContextMenu";
import { PrivateMessageList } from "../components/PrivateMessageList";

export function PrivateChatLayout() {
  //   const [ctxMenu, setCtxMenu] = useState<{
  //     member: ServerMember;
  //     x: number;
  //     y: number;
  //   } | null>(null);

  //   const queryClient = useQueryClient();
  //   const [selectedServerId, setSelectedServerId] = useState<number | null>(null);
  //   const [selectedChannelId, setSelectedChannelId] = useState<number | null>(
  //     null,
  //   );
  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false);
  //   const [serverModalOpen, setServerModalOpen] = useState(false);

  //   const { user } = useAuth();
  //   const { data: servers = [] } = useServers();
  //   const { data: channels = [] } = useChannels(selectedServerId);
  //   const { data: roles = [] } = useServerRoles(selectedServerId);
  //   const { data: members = [] } = useServerMembers(selectedServerId);
  //   const { onlineUserIds } = usePresence(selectedServerId);
  //   const createServer = useCreateServer();
  //   const deleteServer = useDeleteServer();
  //   const leaveServer = useLeaveServer();

  useServerSocket();
  //   usePresenceSocket(selectedServerId);

  //   const serverIds = servers.map((s: Server) => s.id);
  //   const serverNames = Object.fromEntries(
  //     servers.map((s: Server) => [s.id, s.name]),
  //   );

  //   const isDMMode = selectedServerId === null;

  //   // Check if current user is the owner of the selected server
  //   const currentServer = servers.find((s: Server) => s.id === selectedServerId);
  //   const isServerOwner = !!(
  //     currentServer &&
  //     user &&
  //     currentServer.ownerId === user.id
  //   );
  //   const selectedChannel = channels.find(
  //     (channel: Channel) => channel.id === selectedChannelId,
  //   );

  //   const handleDeleteServer = async () => {
  //     if (!selectedServerId) return;
  //     await deleteServer.mutateAsync(selectedServerId);
  //     setSelectedServerId(null);
  //     setSelectedChannelId(null);
  //   };

  //   const handleSelectServer = (id: number | null) => {
  //     setSelectedServerId(id);
  //     setSelectedChannelId(null);
  //   };

  //   // Handler for ServerWizard modal (full data object)
  //   const handleCreateServerFromWizard = async (
  //     data: ServerWizardData,
  //   ): Promise<number | null> => {
  //     const result = await createServer.mutateAsync({ name: data.name });
  //     return result?.id ?? null;
  //   };

  //   // Handler for navigating to a newly created server
  //   const handleGoToServer = (serverId: number) => {
  //     setSelectedServerId(serverId);
  //     setSelectedChannelId(null);
  //   };

  //   // Handler for ServerGate (simple string name - legacy)
  //   const handleCreateServer = async (name: string) => {
  //     const result = await createServer.mutateAsync({ name });
  //     if (result?.id) setSelectedServerId(result.id);
  //   };

  //   const handleJoinServer = async (inviteLink: string) => {
  //     await joinWithInvitation(inviteLink);
  //     queryClient.invalidateQueries({ queryKey: ["servers"] });
  //     setServerModalOpen(false);
  //   };

  //   const handleLeaveServer = async () => {
  //     if (!selectedServerId) return;
  //     try {
  //       await leaveServer.mutateAsync(selectedServerId);
  //       setSelectedServerId(null);
  //       setSelectedChannelId(null);
  //     } catch (error) {
  //       console.error("Impossible de quitter le serveur :", error);
  //     }
  //   };

  return (
    <div className="chat-layout-root">
      {/* Zone 1: Server bar — 72px */}
      <DMRow />
      <div className="chat-dm-wrapper">
        {/* Sidebar DM avec UserBar en bas */}
        <div className="chat-dm-sidebar shrink-0 h-full hidden md:flex flex-col bg-[#2b2d31]">
          <div className="flex-1 overflow-y-auto" />
          <UserBar />
        </div>
        {/* Zone 3: Main content — onglets + contenu (prend le reste) */}
        <div className="flex-1 min-w-0 overflow-hidden">
          <PrivateMessageList />
        </div>
        {/* Zone 4: Right panel — repliable sur tablette */}
        <RightPanel
          collapsed={rightPanelCollapsed}
          onToggle={() => setRightPanelCollapsed((c) => !c)}
        />
        <div className="chat-dm-mobile-userbar md:hidden">
          <UserBar />
        </div>
      </div>
    </div>
  );
}
