package fourthargument.eris.api.controllers;

import java.security.Principal;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.dto.response.PrivateMessagesDTO;
import fourthargument.eris.api.services.MessageService;
import fourthargument.eris.api.services.PrivateMessageService;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.UserException;
import fourthargument.eris.api.services.UserService;
import fourthargument.eris.api.model.User;

@Controller
@RequiredArgsConstructor
public class RealtimeChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final PrivateMessageService privateMessageService;
    private final UserService userService;

    // ── Messages ──
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO messageDTO) throws ChannelException, UserException {
        // Persister via le service existant (channelId vient du DTO)
        MessageDTO saved = messageService.sendMessage(messageDTO, messageDTO.channelId());

        // Broadcast à tous les abonnés du channel
        messagingTemplate.convertAndSend(
                "/topic/channels/" + messageDTO.channelId(), saved);
    }

    // ── Private messages ──
    @MessageMapping("/private.chat")
    public void processPrivateMessage(@Payload PrivateChatPayload payload, Principal principal)
            throws UserException, ConversationException, PrivateMessageException {
        if (principal == null) {
            throw new PrivateMessageException("Unauthenticated websocket session");
        }

        User requester = userService.getUserEntityByEmail(principal.getName());
        PrivateMessagesDTO saved = privateMessageService.sendPrivateMessage(
                payload.conversationId(),
                payload.content(),
                requester);

        messagingTemplate.convertAndSend(
                "/topic/private/" + payload.conversationId(), saved);
    }

    // ── Typing ──
    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingPayload payload) {
        // Relayer à tous les abonnés du channel
        messagingTemplate.convertAndSend(
                "/topic/channels/" + payload.channelId() + "/typing", payload);
    }

    public record TypingPayload(Long userId, String username, Long channelId, boolean typing) {
    }

    public record PrivateChatPayload(Long conversationId, String content) {
    }
}