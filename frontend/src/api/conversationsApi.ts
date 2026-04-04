import { api } from "./client";
import type {
  ConversationPreviewDTO,
  PrivateMessageDTO,
} from "../types/shared";

// ─── Conversation API ─────────────────────────────────────────────────────────

/**
 * Récupère toutes les conversations de l'utilisateur connecté
 * GET /api/private/conversations
 */
export async function getConversations(): Promise<ConversationPreviewDTO[]> {
  const { data } = await api.get<ConversationPreviewDTO[]>(
    "/api/private/conversations",
  );
  return data;
}

/**
 * Crée ou récupère une conversation existante avec un autre utilisateur
 * POST /api/private/conversations
 */
export async function createOrGetConversation(
  receiverId: number,
): Promise<ConversationPreviewDTO> {
  const { data } = await api.post<ConversationPreviewDTO>(
    "/api/private/conversations",
    { receiverId },
  );
  return data;
}

/**
 * Supprime une conversation
 * DELETE /api/private/conversations/{conversationId}
 */
export async function deleteConversation(
  conversationId: number,
): Promise<void> {
  await api.delete(`/api/private/conversations/${conversationId}`);
}

// ─── Private Messages API ─────────────────────────────────────────────────────

/**
 * Récupère tous les messages d'une conversation
 * GET /api/private/conversations/{conversationId}/messages
 */
export async function getConversationMessages(
  conversationId: number,
): Promise<PrivateMessageDTO[]> {
  const { data } = await api.get<PrivateMessageDTO[]>(
    `/api/private/conversations/${conversationId}/messages`,
  );
  return data;
}

/**
 * Envoie un message dans une conversation
 * POST /api/private/conversations/{conversationId}/messages
 */
export async function sendMessage(
  conversationId: number,
  content: string,
): Promise<PrivateMessageDTO> {
  const { data } = await api.post<PrivateMessageDTO>(
    `/api/private/conversations/${conversationId}/messages`,
    { content },
  );
  return data;
}

/**
 * Modifie un message existant
 * PUT /api/private/messages/{messageId}
 */
export async function editMessage(
  messageId: number,
  content: string,
): Promise<PrivateMessageDTO> {
  const { data } = await api.put<PrivateMessageDTO>(
    `/api/private/messages/${messageId}`,
    { content },
  );
  return data;
}

/**
 * Supprime un message
 * DELETE /api/private/messages/{messageId}
 */
export async function deleteMessage(messageId: number): Promise<void> {
  await api.delete(`/api/private/messages/${messageId}`);
}
