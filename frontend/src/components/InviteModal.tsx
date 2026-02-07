import { useState } from "react";

interface InviteModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateInvite: () => void;
  inviteCode: string;
  setInviteCode: (code: string) => void;
  onJoinInvite: () => void;
}

export function InviteModal({
  isOpen,
  onClose,
  onCreateInvite,
}: InviteModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/60 z-50">
      <div className="bg-[#2b2d31] p-6 rounded shadow-lg w-96">
        <h3 className="text-white text-lg mb-4">Server Invites</h3>

        <button
          onClick={onCreateInvite}
          className="w-full mb-4 bg-blue-600 hover:bg-blue-700 text-white py-2 rounded"
        >
          Create Invite
        </button>

        <button
          onClick={onClose}
          className="text-gray-400 hover:text-white text-sm"
        >
          Close
        </button>
      </div>
    </div>
  );
}
