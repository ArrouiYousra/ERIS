import { useState, useEffect } from "react";
import { X, Hash, Lock, Trash2 } from "lucide-react";
import type { Channel } from "../api/channelsApi";

interface ChannelSettingsProps {
  isOpen: boolean;
  channel: Channel | null;
  isPrivate?: boolean;
  onClose: () => void;
  onSave: (data: { name: string; topic: string }) => Promise<void>;
  onDelete: () => Promise<void>;
}

export function ChannelSettings({
  isOpen,
  channel,
  isPrivate = false,
  onClose,
  onSave,
  onDelete,
}: ChannelSettingsProps) {
  const [name, setName] = useState("");
  const [topic, setTopic] = useState("");
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);

  const MAX_TOPIC_LENGTH = 1024;

  // Initialize form when channel changes
  useEffect(() => {
    if (channel) {
      setName(channel.name);
      setTopic(channel.topic ?? "");
      setHasChanges(false);
    }
  }, [channel]);

  // Reset when opening
  useEffect(() => {
    if (isOpen) {
      setShowDeleteConfirm(false);
    }
  }, [isOpen]);

  // Handle escape key
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) {
        if (showDeleteConfirm) {
          setShowDeleteConfirm(false);
        } else {
          onClose();
        }
      }
    };
    window.addEventListener("keydown", handleEscape);
    return () => window.removeEventListener("keydown", handleEscape);
  }, [isOpen, onClose, showDeleteConfirm]);

  if (!isOpen || !channel) return null;

  const handleNameChange = (value: string) => {
    const formatted = value.toLowerCase().replace(/\s+/g, "-");
    setName(formatted);
    setHasChanges(formatted !== channel.name || topic !== (channel.topic ?? ""));
  };

  const handleTopicChange = (value: string) => {
    if (value.length <= MAX_TOPIC_LENGTH) {
      setTopic(value);
      setHasChanges(name !== channel.name || value !== (channel.topic ?? ""));
    }
  };

  const handleSave = async () => {
    setIsSaving(true);
    try {
      await onSave({ name, topic });
      setHasChanges(false);
    } catch (error) {
      console.error("Error saving channel:", error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await onDelete();
      onClose();
    } catch (error) {
      console.error("Error deleting channel:", error);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex bg-[#313338]">
      {/* Left spacer */}
      <div className="flex-1 bg-[#2b2d31] max-w-[200px]" />
      
      {/* Sidebar */}
      <div className="w-[232px] bg-[#2b2d31] flex flex-col">
        {/* Channel header */}
        <div className="px-4 py-6">
          <div className="flex items-center gap-1.5 text-gray-400 text-xs font-semibold uppercase tracking-wide">
            {isPrivate ? <Lock className="w-3 h-3" /> : <Hash className="w-3 h-3" />}
            <span className="truncate">{channel.name}</span>
            <span className="text-gray-500 ml-1">SALONS TEXTUELS</span>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-2">
          <button
            className="w-full text-left px-2.5 py-1.5 rounded text-sm mb-0.5 bg-white/10 text-white"
          >
            Vue d'ensemble
          </button>

          {/* Separator */}
          <div className="my-4 border-t border-white/10" />

          {/* Delete button */}
          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="w-full flex items-center justify-between px-2.5 py-1.5 rounded text-sm text-red-400 hover:text-red-300 hover:bg-red-500/10 transition-colors"
          >
            <span>Supprimer le salon</span>
            <Trash2 className="w-4 h-4" />
          </button>
        </nav>
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col overflow-hidden relative">
        {/* Close button - positioned at far right */}
        <button
          onClick={onClose}
          className="absolute top-6 right-6 flex flex-col items-center gap-1 text-gray-400 hover:text-white transition-colors z-10"
        >
          <div className="w-9 h-9 rounded-full border-2 border-gray-500 hover:border-white flex items-center justify-center transition-colors">
            <X className="w-5 h-5" />
          </div>
          <span className="text-[10px] font-medium">ESC</span>
        </button>

        {/* Content area */}
        <div className="flex-1 overflow-y-auto">
          <div className="max-w-[900px] p-10 pr-24">
            {/* Header */}
            <h2 className="text-xl font-semibold text-white mb-6">
              Vue d'ensemble
            </h2>

            <div className="space-y-6">
              {/* Channel name */}
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-2">
                  Nom du salon
                </label>
                <div className="relative">
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => handleNameChange(e.target.value)}
                    className="w-full px-3 py-2.5 bg-[#1e1f22] rounded text-white placeholder-gray-500 outline-none pr-10"
                  />
                  {name && (
                    <button
                      onClick={() => handleNameChange("")}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white transition-colors"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  )}
                </div>
              </div>

              {/* Channel topic */}
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase mb-2">
                  Sujet du salon
                </label>
                <div className="relative">
                  <textarea
                    value={topic}
                    onChange={(e) => handleTopicChange(e.target.value)}
                    placeholder="Décrivez le sujet de ce salon..."
                    rows={8}
                    className="w-full px-3 py-2.5 bg-[#1e1f22] rounded text-white placeholder-gray-500 outline-none resize-none"
                  />
                  <span className="absolute bottom-2 right-3 text-xs text-gray-500">
                    {topic.length}
                  </span>
                </div>
              </div>

            </div>
          </div>
        </div>

        {/* Save bar (appears when changes are made) */}
        {hasChanges && (
          <div className="px-4 py-3 bg-[#1e1f22] border-t border-black/20 flex items-center justify-between">
            <p className="text-sm text-gray-300">
              Attention — tu as des modifications non enregistrées !
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => {
                  setName(channel.name);
                  setTopic(channel.topic ?? "");
                  setHasChanges(false);
                }}
                className="px-4 py-1.5 text-sm text-gray-300 hover:text-white hover:underline transition-colors"
              >
                Réinitialiser
              </button>
              <button
                onClick={handleSave}
                disabled={isSaving || !name.trim()}
                className="px-4 py-1.5 bg-[#248046] hover:bg-[#1a6334] disabled:bg-[#248046]/50 disabled:cursor-not-allowed text-white text-sm font-medium rounded transition-colors"
              >
                {isSaving ? "Enregistrement..." : "Enregistrer les modifications"}
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Delete confirmation modal */}
      {showDeleteConfirm && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/70"
          onClick={() => setShowDeleteConfirm(false)}
        >
          <div
            className="bg-[#313338] rounded-lg p-5 max-w-sm w-full mx-4 shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            <h3 className="text-lg font-semibold text-white mb-2">
              Supprimer le salon
            </h3>
            <p className="text-gray-400 text-sm mb-4">
              Es-tu sûr de vouloir supprimer{" "}
              <span className="text-white font-medium">#{channel.name}</span> ?
              Cette action est irréversible.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowDeleteConfirm(false)}
                className="flex-1 px-4 py-2 text-gray-300 hover:text-white hover:underline transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={handleDelete}
                disabled={isDeleting}
                className="flex-1 px-4 py-2 bg-red-500 hover:bg-red-600 disabled:bg-red-500/50 text-white font-medium rounded transition-colors"
              >
                {isDeleting ? "Suppression..." : "Supprimer le salon"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
