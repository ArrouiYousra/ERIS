import { useState } from "react";
import { useCreateChannel } from "../hooks/useChannels";
import type { Channel } from "../api/channelsApi";
import "../styles/channelList.css";

interface ChannelListProps {
  serverId: number | null;
  channels?: Channel[];
  onSelectChannel: (channelId: number) => void;
  selectedChannelId?: number | null;
}

export function ChannelList({
  serverId,
  channels = [],
  onSelectChannel,
  selectedChannelId,
}: ChannelListProps) {
  const [showCreateChannel, setShowCreateChannel] = useState(false);
  const [newChannelName, setNewChannelName] = useState("");
  const createChannel = useCreateChannel();

  if (!serverId) {
    return (
      <div className="channel-list">
        <div className="channel-list-empty">Message privés</div>
      </div>
    );
  }

  const handleCreateChannel = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newChannelName.trim()) return;

    try {
      await createChannel.mutateAsync({
        serverId,
        payload: { name: newChannelName, serverId },
      });
      setNewChannelName("");
      setShowCreateChannel(false);
    } catch (error) {
      console.error("Error creating channel:", error);
    }
  };

  return (
    <div className="channel-list">
      <div className="channel-list-header">
        <h3 className="channel-list-title">Channels</h3>
        <button
          className="channel-list-add-button"
          onClick={() => setShowCreateChannel(!showCreateChannel)}
          title="Create channel"
        >
          +
        </button>
      </div>

      {showCreateChannel && (
        <form className="channel-create-form" onSubmit={handleCreateChannel}>
          <input
            type="text"
            className="channel-create-input"
            placeholder="Channel name"
            value={newChannelName}
            onChange={(e) => setNewChannelName(e.target.value)}
            autoFocus
          />
          <div className="channel-create-actions">
            <button
              type="submit"
              className="channel-create-submit"
              disabled={createChannel.isPending}
            >
              Create
            </button>
            <button
              type="button"
              className="channel-create-cancel"
              onClick={() => {
                setShowCreateChannel(false);
                setNewChannelName("");
              }}
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      <div className="channel-list-items">
        {channels.length > 0 ? (
          channels.map((channel) => (
            <div
              key={channel.id}
              className={`channel-item ${selectedChannelId === channel.id ? "channel-item--selected" : ""}`}
              onClick={() => onSelectChannel(channel.id)}
            >
              <span className="channel-item-prefix">#</span>
              <span className="channel-item-name">{channel.name}</span>
            </div>
          ))
        ) : (
          <div className="channel-list-empty">No channels yet</div>
        )}
      </div>
    </div>
  );
}
