import { useState } from "react";
import { useChannels } from "../hooks/useChannels";
import {
  useServers,
  useCreateServer,
  useDeleteServer,
} from "../hooks/useServers";
import { useAuth } from "../hooks/useAuth";
import { useQueryClient } from "@tanstack/react-query";
import {
  ServerBar,
  RightPanel,
  UserBar,
} from "../components/friends";
import { ChannelList } from "../components/ChannelList";
import { MessageList } from "../components/MessageList";
import { ServerGate } from "../components/ServerGate";
import {
  ServerWizard,
  type ServerWizardData,
} from "../components/ServerWizard";
import type { MainContentTab } from "../types/friends";
import "../styles/chat.css";
import "../styles/serverWizard.css";
import { joinWithInvitation } from "../api/invitationApi";
import { useServerMembers } from "../hooks/useServers";
import { usePresence } from "../hooks/usePresence";

export function ChatLayout() {
  const queryClient = useQueryClient();
  const [selectedServerId, setSelectedServerId] = useState<number | null>(null);
  const [selectedChannelId, setSelectedChannelId] = useState<number | null>(
    null,
  );
  
  const { data: members = [] } = useServerMembers(selectedServerId);
  const { onlineUserIds } = usePresence(selectedServerId);
  const [selectedDMId, setSelectedDMId] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<MainContentTab>("ADD");
  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false);
  const [serverModalOpen, setServerModalOpen] = useState(false);
  const [showMemberList, setShowMemberList] = useState(true);

  const { user } = useAuth();
  const { data: servers = [] } = useServers();
  const { data: channels = [] } = useChannels(selectedServerId);
  const createServer = useCreateServer();
  const deleteServer = useDeleteServer();

  const serverIds = servers.map((s: { id: number }) => s.id);
  const serverNames = Object.fromEntries(
    servers.map((s: { id: number; name: string }) => [s.id, s.name]),
  );

  const isDMMode = selectedServerId === null;

  // Check if current user is the owner of the selected server
  const currentServer = servers.find((s: any) => s.id === selectedServerId);
  const isServerOwner = !!(currentServer && user && currentServer.ownerId === user.id);

  const handleDeleteServer = async () => {
    if (!selectedServerId) return;
    await deleteServer.mutateAsync(selectedServerId);
    setSelectedServerId(null);
    setSelectedChannelId(null);
  };

  const handleSelectServer = (id: number | null) => {
    setSelectedServerId(id);
    if (id !== null) {
      setSelectedDMId(null);
    } else {
      setSelectedChannelId(null);
    }
  };

  // Handler for ServerWizard modal (full data object)
  const handleCreateServerFromWizard = async (
    data: ServerWizardData,
  ): Promise<number | null> => {
    const result = await createServer.mutateAsync({ name: data.name });
    console.log("Server created, API response:", result);
    console.log("Server ID from response:", result?.id);
    return result?.id ?? null;
  };

  // Handler for navigating to a newly created server
  const handleGoToServer = (serverId: number) => {
    console.log("handleGoToServer called with serverId:", serverId);
    console.log("Current serverIds in list:", serverIds);
    setSelectedServerId(serverId);
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

  return (
    <div
      className="chat-layout-root bg-[#0f1115] text-[#f2f3f5]"
      style={{
        width: "100%",
        minWidth: 0,
        flex: 1,
        height: "100%",
        minHeight: 0,
        display: "flex",
        flexDirection: "row",
        overflow: "hidden",
        boxSizing: "border-box",
      }}
    >
      {/* Zone 1: Server bar — 72px */}
      <ServerBar
        selectedServerId={selectedServerId}
        onSelectServer={handleSelectServer}
        serverIds={serverIds}
        serverNames={serverNames}
        onAddServer={() => setServerModalOpen(true)}
      />

      {isDMMode ? (
        <div
          className="chat-dm-wrapper"
          style={{
            flex: 1,
            minWidth: 0,
            display: "flex",
            flexDirection: "row",
            overflow: "hidden",
          }}
        >
          {/* Sidebar DM avec UserBar en bas */}
          <div className="w-[240px] min-w-[240px] shrink-0 h-full flex flex-col bg-[#2b2d31]">
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
        </div>
      ) : (
        <div className="flex flex-row flex-1 h-full min-w-0 overflow-hidden">
          {/* Mode serveur : sidebar canaux + zone messages */}
          <div className="chat-sidebar chat-sidebar--channels w-[240px] min-w-[240px] shrink-0 h-full">
            <ChannelList
              serverId={selectedServerId}
              channels={channels}
              onSelectChannel={setSelectedChannelId}
              selectedChannelId={selectedChannelId}
              serverName={
                selectedServerId ? serverNames[selectedServerId] : "Serveur"
              }
              isOwner={isServerOwner}
              onDeleteServer={handleDeleteServer}
            />
          </div>
          <div className="chat-main flex-1 flex flex-row min-w-0 h-full bg-[#313338]">
            <div className="flex-1 flex flex-col min-w-0 h-full">
              <MessageList
                channelId={selectedChannelId}
                channelName={channels.find((c: any) => c.id === selectedChannelId)?.name}
                channelTopic={(channels.find((c: any) => c.id === selectedChannelId) as any)?.topic}
                isPrivate={(channels.find((c: any) => c.id === selectedChannelId) as any)?.isPrivate}
                serverName={selectedServerId ? serverNames[selectedServerId] : "Serveur"}
                showMemberList={showMemberList}
                onToggleMemberList={() => setShowMemberList(!showMemberList)}
              />
            </div>
            {showMemberList && (() => {
              const onlineMembers = members.filter((m: any) => onlineUserIds.has(m.userId));
              const offlineMembers = members.filter((m: any) => !onlineUserIds.has(m.userId));
              return (
                <div className="w-60 h-full bg-[#2b2d31] border-l border-black/20 overflow-y-auto shrink-0">
                  <div className="p-4 space-y-3">
                    {/* En ligne */}
                    {onlineMembers.length > 0 && (
                      <div>
                        <h3 className="text-xs font-semibold text-gray-400 uppercase mb-2">
                          En ligne — {onlineMembers.length}
                        </h3>
                        <div className="space-y-0.5">
                          {onlineMembers.map((member: any) => (
                            <div key={member.userId} className="flex items-center gap-3 p-1.5 rounded hover:bg-white/5 cursor-pointer">
                              <div className="relative">
                                <div className="w-8 h-8 rounded-full bg-[#5865F2] flex items-center justify-center text-white text-sm font-medium">
                                  {(member.nickname || member.username || "U").charAt(0).toUpperCase()}
                                </div>
                                <div className="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-[#23a559] rounded-full border-2 border-[#2b2d31]" />
                              </div>
                              <span className="text-gray-300 text-sm">{member.nickname || member.username || `User ${member.userId}`}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                    {/* Hors ligne */}
                    {offlineMembers.length > 0 && (
                      <div>
                        <h3 className="text-xs font-semibold text-gray-400 uppercase mb-2">
                          Hors ligne — {offlineMembers.length}
                        </h3>
                        <div className="space-y-0.5">
                          {offlineMembers.map((member: any) => (
                            <div key={member.userId} className="flex items-center gap-3 p-1.5 rounded hover:bg-white/5 cursor-pointer opacity-40">
                              <div className="relative">
                                <div className="w-8 h-8 rounded-full bg-[#5865F2] flex items-center justify-center text-white text-sm font-medium">
                                  {(member.nickname || member.username || "U").charAt(0).toUpperCase()}
                                </div>
                                <div className="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-[#80848e] rounded-full border-2 border-[#2b2d31]" />
                              </div>
                              <span className="text-gray-300 text-sm">{member.nickname || member.username || `User ${member.userId}`}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
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
