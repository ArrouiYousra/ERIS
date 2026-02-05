import { useState } from "react";
import { useChannels } from "../hooks/useChannels";
import {
  useServers,
  useCreateServer,
  useServerMember,
} from "../hooks/useServers";
import { joinServerByInviteLink } from "../api/serversApi";
import { useQueryClient } from "@tanstack/react-query";
import {
  ServerBar,
  MainSidebar,
  MainContent,
  RightPanel,
} from "../components/friends";
import { ChannelList } from "../components/ChannelList";
import { MessageList } from "../components/MessageList";
import { ServerGate } from "../components/ServerGate";
import type { MainContentTab } from "../types/friends";
import "../styles/chat.css";
import { LeftPanel } from "../components/friends/LeftPanel";

export function ChatLayout() {
  const queryClient = useQueryClient();
  const [selectedServerId, setSelectedServerId] = useState<number | null>(null);
  const [selectedChannelId, setSelectedChannelId] = useState<number | null>(
    null,
  );
  const [selectedDMId, setSelectedDMId] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<MainContentTab>("ADD");
  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false);
  const [serverModalOpen, setServerModalOpen] = useState(false);
  const [serverMembers, setServerMembers] = useState<number | null>(null);

  const { data: servers = [] } = useServers();
  const { data: channels = [] } = useChannels(selectedServerId);
  const createServer = useCreateServer();

  const serverIds = servers.map((s: { id: number }) => s.id);
  const serverNames = Object.fromEntries(
    servers.map((s: { id: number; name: string }) => [s.id, s.name]),
  );

  const isDMMode = selectedServerId === null;

  const handleSelectServer = (id: number | null) => {
    setSelectedServerId(id);
    if (id !== null) {
      setSelectedDMId(null);
    } else {
      setSelectedChannelId(null);
    }
  };

  const handleCreateServer = async (name: string) => {
    const result = await createServer.mutateAsync({ name });
    if (result?.id) setSelectedServerId(result.id);
    setServerModalOpen(false);
  };

  const handleJoinServer = async (inviteLink: string) => {
    await joinServerByInviteLink(inviteLink);
    queryClient.invalidateQueries({ queryKey: ["servers"] });
    setServerModalOpen(false);
  };

  const handleServerMember = async (id: number | null) => {
    setServerMembers(id);
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
          {/* Zone 2: Main sidebar — Amis / Messages privés
          <MainSidebar
            selectedDMId={selectedDMId}
            onSelectDM={setSelectedDMId}
          /> */}
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
        <>
          {/* Mode serveur : sidebar canaux + zone messages */}
          <div className="chat-sidebar chat-sidebar--channels w-[240px] min-w-[240px] shrink-0">
            <ChannelList
              serverId={selectedServerId}
              channels={channels}
              onSelectChannel={setSelectedChannelId}
              selectedChannelId={selectedChannelId}
            />
          </div>
          <div className="chat-main flex-1 flex flex-col min-w-0 bg-[#313338]">
            <MessageList channelId={selectedChannelId} />
          </div>
          <div>
            {" "}
            {
              <LeftPanel
                serverId={selectedServerId}
                collapsed={rightPanelCollapsed}
                onToggle={() => setRightPanelCollapsed((c) => !c)}
              />
            }
          </div>
        </>
      )}
    </div>
  );
}
