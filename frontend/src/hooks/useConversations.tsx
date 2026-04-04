import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getConversations,
  createOrGetConversation,
  deleteConversation,
  getConversationMessages,
  sendMessage,
  editMessage,
  deleteMessage,
} from "../api/conversationsApi";
import type {
  ConversationPreviewDTO,
  PrivateMessageDTO,
} from "../types/shared";

// ─── Query Keys ─────────────────────────────────────────────────────────────

const CONVERSATIONS_KEY = "conversations";
const MESSAGES_KEY = "messages";

// ─── Conversations ────────────────────────────────────────────────────────────

/**
 * Hook pour récupérer toutes les conversations de l'utilisateur
 * @param enabled - Détermine si la requête doit être exécutée (ex: user authentifié)
 */
export function useConversations(enabled: boolean = true) {
  return useQuery<ConversationPreviewDTO[]>({
    queryKey: [CONVERSATIONS_KEY],
    queryFn: getConversations,
    enabled,
  });
}

/**
 * Mutation pour créer ou récupérer une conversation
 */
export function useCreateConversation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (receiverId: number) => createOrGetConversation(receiverId),
    onSuccess: () => {
      // Invalide le cache des conversations pour recharger la liste
      queryClient.invalidateQueries({ queryKey: [CONVERSATIONS_KEY] });
    },
  });
}

/**
 * Mutation pour supprimer une conversation
 */
export function useDeleteConversation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (conversationId: number) => deleteConversation(conversationId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CONVERSATIONS_KEY] });
    },
  });
}

// ─── Messages ─────────────────────────────────────────────────────────────────

/**
 * Hook pour récupérer les messages d'une conversation
 */
export function useConversationMessages(conversationId: number | null) {
  return useQuery<PrivateMessageDTO[]>({
    queryKey: [MESSAGES_KEY, conversationId],
    queryFn: () =>
      conversationId ? getConversationMessages(conversationId) : [],
    enabled: !!conversationId,
  });
}

/**
 * Mutation pour envoyer un message
 */
export function useSendMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      conversationId,
      content,
    }: {
      conversationId: number;
      content: string;
    }) => sendMessage(conversationId, content),
    onSuccess: (_, variables) => {
      // Invalide le cache des messages de cette conversation
      queryClient.invalidateQueries({
        queryKey: [MESSAGES_KEY, variables.conversationId],
      });
      // Invalide aussi les conversations pour mettre à jour le dernier message
      queryClient.invalidateQueries({ queryKey: [CONVERSATIONS_KEY] });
    },
  });
}

/**
 * Mutation pour modifier un message
 */
export function useEditMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      messageId,
      content,
    }: {
      messageId: number;
      conversationId: number;
      content: string;
    }) => editMessage(messageId, content),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: [MESSAGES_KEY, variables.conversationId],
      });
    },
  });
}

/**
 * Mutation pour supprimer un message
 */
export function useDeleteMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      messageId,
    }: {
      messageId: number;
      conversationId: number;
    }) => deleteMessage(messageId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: [MESSAGES_KEY, variables.conversationId],
      });
      queryClient.invalidateQueries({ queryKey: [CONVERSATIONS_KEY] });
    },
  });
}
