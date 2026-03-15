 import { useState, useRef, useEffect } from "react";
import {
  Plus,
  Hash,
  Lock,
  ChevronDown,
  UserPlus,
  Trash2,
} from "lucide-react";
import {
  useCreateChannel,
  useDeleteChannel,
} from "../hooks/useChannels";
import { ChannelWizard, type ChannelWizardData } from "./ChannelWizard";
import type { Channel } from "../api/channelsApi";
import {
  createInvitation,
  joinWithInvitation,
  type InvitationDTO,
} from "../api/invitationApi";
import { InviteModal } from "./InviteModal";
import { UserBar } from "./friends/UserBar";

interface ChannelListProps {
  serverId: number | null;
  channels?: Channel[];
  onSelectChannel: (channelId: number | null) => void;
  selectedChannelId?: number | null;
  serverName?: string;
  serverMembers?: { id: number; username: string }[];
  serverRoles?: { id: number; name: string }[];
  isOwner?: boolean;
  onDeleteServer?: () => void;
}

export function ChannelList({
  serverId,
  channels = [],
  onSelectChannel,
  selectedChannelId,
  serverName = "Serveur",
  serverMembers = [],
  serverRoles = [],
  isOwner = false,
  onDeleteServer,
}: ChannelListProps) {
  const [showChannelWizard, setShowChannelWizard] = useState(false);
  const [channelsCategoryOpen, setChannelsCategoryOpen] = useState(true);
  const createChannel = useCreateChannel();
  const deleteChannel = useDeleteChannel();
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteCode, setInviteCode] = useState("");
  const [showServerDropdown, setShowServerDropdown] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown on click outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setShowServerDropdown(false);
      }
    };
    if (showServerDropdown) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showServerDropdown]);

  if (!serverId) {
    return (
      <div className="channel-list">
        <div className="channel-list-empty">Messages prives</div>
      </div>
    );
  }

  const handleCreateChannel = async (
    data: ChannelWizardData,
  ): Promise<number | null> => {
    try {
      const result = await createChannel.mutateAsync({
        serverId,
        payload: { name: data.name, serverId },
      });
      return result?.id ?? null;
    } catch (error) {
      console.error("Error creating channel:", error);
      return null;
    }
  };

  const handleGoToChannel = (channelId: number) => {
    onSelectChannel(channelId);
  };

  const handleDeleteChannel = async (channelId: number, channelName: string) => {
    if (!serverId) return;
    const confirmed = window.confirm(
      `Supprimer le salon "${channelName}" ? Cette action est irreversible.`,
    );
    if (!confirmed) return;

    await deleteChannel.mutateAsync({
      channelId,
      serverId,
    });

    // If the deleted channel was selected, select another one or go back to server view
    if (selectedChannelId === channelId) {
      const remainingChannels = channels.filter(
        (c) => c.id !== channelId,
      );
      onSelectChannel(remainingChannels[0]?.id ?? null);
    }
  };

  const handleCreateInvite = async () => {
    if (!serverId) return;

    try {
      const newInvite: InvitationDTO = await createInvitation(serverId);
      alert(`Invitation creee ! Code : ${newInvite.code}`);
      // Optionally: copy to clipboard
      navigator.clipboard.writeText(newInvite.code);
    } catch (err) {
      console.error("Echec de creation de l'invitation :", err);
    }
  };

  const handleJoinWithInvite = async () => {
    if (!inviteCode) return;

    try {
      const response = await joinWithInvitation(inviteCode);
      alert(`Serveur rejoint : ${response.serverName}`);
      setInviteCode(""); // clear input
      setShowInviteModal(false); // close modal if using one
      // Optionally: refresh server list / reload server data
    } catch (err) {
      console.error("Echec de connexion via invitation :", err);
      alert("Code d'invitation invalide ou expire");
    }
  };

  return (
    <div className="flex flex-col h-full bg-[#2b2d31]">
      {/* Server header with dropdown */}
      <div className="relative" ref={dropdownRef}>
        <div
          onClick={() => setShowServerDropdown(!showServerDropdown)} // toggle the server dropdown
          className="h-12 px-4 flex items-center justify-between border-b border-black/20 shadow-sm hover:bg-[#35373c] cursor-pointer transition-colors group"
        >
          <div className="flex items-center gap-1 flex-1 min-w-0">
            <ChevronDown className={`w-4 h-4 text-gray-400 shrink-0 transition-transform ${showServerDropdown ? "rotate-180" : ""}`} />
            <h2 className="text-white font-semibold truncate">{serverName}</h2>
          </div>
        </div>

        {/* Server dropdown menu */}
        {showServerDropdown && (
          <div className="absolute top-12 left-2 right-2 bg-[#111214] rounded-lg shadow-xl border border-white/10 py-1.5 z-50">
            <button
              onClick={() => { setShowInviteModal(true); setShowServerDropdown(false); }}
              className="w-full flex items-center gap-2 px-3 py-2 text-sm text-[#b5bac1] hover:bg-[#5865F2] hover:text-white rounded-sm mx-1 transition-colors"
              style={{ width: "calc(100% - 8px)" }}
            >
              <UserPlus className="w-4 h-4" />
              Inviter des personnes
            </button>
            {isOwner && (
              <>
                <div className="mx-2 my-1 border-t border-white/10" />
                <button
                  onClick={() => { setShowDeleteConfirm(true); setShowServerDropdown(false); }}
                  className="w-full flex items-center gap-2 px-3 py-2 text-sm text-red-400 hover:bg-red-500 hover:text-white rounded-sm mx-1 transition-colors"
                  style={{ width: "calc(100% - 8px)" }}
                >
                  <Trash2 className="w-4 h-4" />
                  Supprimer le serveur
                </button>
              </>
            )}
          </div>
        )}
      </div>

      {/* Delete server confirmation modal */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/60 z-[9999]">
          <div className="bg-[#313338] rounded-lg p-6 w-[92vw] max-w-[440px] shadow-xl">
            <h3 className="text-xl font-bold text-white mb-2">Supprimer '{serverName}'</h3>
            <p className="text-[#b5bac1] text-sm mb-6">
              Es-tu sur de vouloir supprimer <strong className="text-white">{serverName}</strong> ? Cette action est irreversible. Tous les salons, messages et membres seront supprimes.
            </p>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setShowDeleteConfirm(false)}
                className="px-4 py-2 text-sm text-white hover:underline"
              >
                Annuler
              </button>
              <button
                onClick={() => {
                  onDeleteServer?.();
                  setShowDeleteConfirm(false);
                }}
                className="px-4 py-2 text-sm bg-red-500 hover:bg-red-600 text-white rounded transition-colors font-medium"
              >
                Supprimer le serveur
              </button>
            </div>
          </div>
        </div>
      )}

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
                const isPrivate = channel.isPrivate;
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
                      <Lock
                        className={`w-5 h-5 shrink-0 ${isSelected ? "text-gray-300" : "text-gray-400"}`}
                      />
                    ) : (
                      <Hash
                        className={`w-5 h-5 shrink-0 ${isSelected ? "text-gray-300" : "text-gray-400"}`}
                      />
                    )}
                    <span
                      className={`truncate text-[15px] flex-1 ${isSelected ? "font-medium" : ""}`}
                    >
                      {channel.name}
                    </span>
                    <div
                      className={`flex items-center gap-1 ${isSelected ? "opacity-100" : "opacity-0 group-hover:opacity-100"} transition-opacity`}
                    >
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          void handleDeleteChannel(channel.id, channel.name);
                        }}
                        className="icon-btn text-red-400 hover:text-red-300 transition-colors"
                        title="Supprimer le salon"
                      >
                        <Trash2 className="w-4 h-4" />
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

<<<<<<< HEAD
      {/* Channel settings page */}
      <ChannelSettings
        isOpen={!!channelToEdit}
        channel={channelToEdit}
        isPrivate={channelToEdit?.isPrivate}
        onClose={() => setChannelToEdit(null)}
        onSave={handleSaveChannel}
        onDelete={handleDeleteChannel}
      />

=======
>>>>>>> eeca5dd (task: Improve mobile chat UX)
      {/* Channel creation wizard */}
      <ChannelWizard
        isOpen={showChannelWizard}
        onClose={() => setShowChannelWizard(false)}
        onCreateChannel={handleCreateChannel}
        onGoToChannel={handleGoToChannel}
        serverMembers={serverMembers}
        serverRoles={serverRoles}
      />
      <InviteModal
        isOpen={showInviteModal}
        onClose={() => setShowInviteModal(false)}
        onCreateInvite={handleCreateInvite}
        inviteCode={inviteCode}
        setInviteCode={setInviteCode}
        onJoinInvite={handleJoinWithInvite}
      />

      {/* User panel */}
      <UserBar />
    </div>
  );
}
