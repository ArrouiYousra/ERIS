import { useState } from "react";
import { Plus, Hash, Lock, ChevronDown, Settings, UserPlus } from "lucide-react";
import { useCreateChannel, useUpdateChannel, useDeleteChannel } from "../hooks/useChannels";
import { ChannelWizard, type ChannelWizardData } from "./ChannelWizard";
import { ChannelSettings } from "./ChannelSettings";
import type { Channel } from "../api/channelsApi";

interface ChannelListProps {
  serverId: number | null;
  channels?: Channel[];
  onSelectChannel: (channelId: number) => void;
  selectedChannelId?: number | null;
  serverName?: string;
  serverMembers?: { id: number; username: string }[];
  serverRoles?: { id: number; name: string }[];
}

export function ChannelList({
  serverId,
  channels = [],
  onSelectChannel,
  selectedChannelId,
  serverName = "Serveur",
  serverMembers = [],
  serverRoles = [],
}: ChannelListProps) {
  const [showChannelWizard, setShowChannelWizard] = useState(false);
  const [channelsCategoryOpen, setChannelsCategoryOpen] = useState(true);
  const [channelToEdit, setChannelToEdit] = useState<Channel | null>(null);
  const createChannel = useCreateChannel();
  const updateChannel = useUpdateChannel();
  const deleteChannel = useDeleteChannel();

  if (!serverId) {
    return (
      <div className="channel-list">
        <div className="channel-list-empty">Message privés</div>
      </div>
    );
  }

  const handleCreateChannel = async (data: ChannelWizardData): Promise<number | null> => {
    console.log("Creating channel with data:", data);
    console.log("ServerId:", serverId);
    try {
      const result = await createChannel.mutateAsync({
        serverId,
        payload: { name: data.name, serverId },
      });
      console.log("Channel created, result:", result);
      return result?.id ?? null;
    } catch (error) {
      console.error("Error creating channel:", error);
      return null;
    }
  };

  const handleGoToChannel = (channelId: number) => {
    onSelectChannel(channelId);
  };

  const handleSaveChannel = async (data: { name: string; topic: string }) => {
    if (!channelToEdit || !serverId) return;
    await updateChannel.mutateAsync({
      channelId: channelToEdit.id,
      serverId,
      payload: { name: data.name, topic: data.topic },
    });
    // Update the local channel reference with new data
    setChannelToEdit({ ...channelToEdit, name: data.name, topic: data.topic });
  };

  const handleDeleteChannel = async () => {
    if (!channelToEdit || !serverId) return;
    await deleteChannel.mutateAsync({
      channelId: channelToEdit.id,
      serverId,
    });
    // If the deleted channel was selected, deselect it
    if (selectedChannelId === channelToEdit.id) {
      const remainingChannels = channels.filter(c => c.id !== channelToEdit.id);
      onSelectChannel(remainingChannels[0]?.id ?? 0);
    }
    setChannelToEdit(null);
  };

  return (
    <div className="flex flex-col h-full bg-[#2b2d31]">
      {/* Server header */}
      <div className="h-12 px-4 flex items-center justify-between border-b border-black/20 shadow-sm hover:bg-[#35373c] cursor-pointer transition-colors group">
        <div className="flex items-center gap-1 flex-1 min-w-0">
          <h2 className="text-white font-semibold truncate">{serverName}</h2>
          <ChevronDown className="w-4 h-4 text-gray-400 shrink-0" />
        </div>
        <button
          onClick={(e) => {
            e.stopPropagation();
            // TODO: Open invite modal
          }}
          className="icon-btn opacity-0 group-hover:opacity-100 text-gray-400 hover:text-white transition-all"
          title="Inviter des personnes"
        >
          <UserPlus className="w-5 h-5" />
        </button>
      </div>

      {/* Channels section */}
      <div className="flex-1 overflow-y-auto py-3">
        {/* Category header */}
        <div className="px-2 mb-1">
          <div
            onClick={() => setChannelsCategoryOpen(!channelsCategoryOpen)}
            className="flex items-center gap-1 w-full group cursor-pointer"
          >
            <ChevronDown 
              className={`w-3 h-3 text-gray-400 group-hover:text-gray-200 transition-all ${
                channelsCategoryOpen ? "" : "-rotate-90"
              }`} 
            />
            <span className="text-xs font-semibold text-gray-400 group-hover:text-gray-200 uppercase tracking-wide transition-colors">
              Salons textuels
            </span>
            <button
              onClick={(e) => {
                e.stopPropagation();
                setShowChannelWizard(true);
              }}
              className="icon-btn ml-auto opacity-0 group-hover:opacity-100 text-gray-400 hover:text-gray-200 transition-all"
              title="Créer un salon"
            >
              <Plus className="w-[18px] h-[18px]" />
            </button>
          </div>
        </div>

        {/* Channels list */}
        {channelsCategoryOpen && (
          <div className="px-2 space-y-0.5">
            {channels.length > 0 ? (
              channels.map((channel) => {
                const isSelected = selectedChannelId === channel.id;
                const isPrivate = (channel as any).isPrivate;
                return (
                  <div
                    key={channel.id}
                    className={`group w-full flex items-center gap-1.5 px-2 py-1 rounded text-left transition-colors cursor-pointer ${
                      isSelected
                        ? "bg-[#404249] text-white"
                        : "text-gray-400 hover:text-gray-200 hover:bg-[#35373c]"
                    }`}
                    onClick={() => onSelectChannel(channel.id)}
                  >
                    {isPrivate ? (
                      <Lock className={`w-5 h-5 shrink-0 ${isSelected ? "text-gray-300" : "text-gray-400"}`} />
                    ) : (
                      <Hash className={`w-5 h-5 shrink-0 ${isSelected ? "text-gray-300" : "text-gray-400"}`} />
                    )}
                    <span className={`truncate text-[15px] flex-1 ${isSelected ? "font-medium" : ""}`}>{channel.name}</span>
                    <div className={`flex items-center gap-1 ${isSelected ? "opacity-100" : "opacity-0 group-hover:opacity-100"} transition-opacity`}>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          // TODO: Invite functionality
                        }}
                        className="icon-btn text-gray-400 hover:text-gray-200 transition-colors"
                        title="Créer une invitation"
                      >
                        <UserPlus className="w-4 h-4" />
                      </button>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setChannelToEdit(channel);
                        }}
                        className="icon-btn text-gray-400 hover:text-gray-200 transition-colors"
                        title="Modifier le salon"
                      >
                        <Settings className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })
            ) : (
              <p className="text-gray-500 text-sm text-center py-4">
                Aucun salon
              </p>
            )}
          </div>
        )}
      </div>

      {/* Channel settings page */}
      <ChannelSettings
        isOpen={!!channelToEdit}
        channel={channelToEdit}
        isPrivate={(channelToEdit as any)?.isPrivate}
        onClose={() => setChannelToEdit(null)}
        onSave={handleSaveChannel}
        onDelete={handleDeleteChannel}
      />

      {/* Channel creation wizard */}
      <ChannelWizard
        isOpen={showChannelWizard}
        onClose={() => setShowChannelWizard(false)}
        onCreateChannel={handleCreateChannel}
        onGoToChannel={handleGoToChannel}
        serverMembers={serverMembers}
        serverRoles={serverRoles}
      />
    </div>
  );
}
