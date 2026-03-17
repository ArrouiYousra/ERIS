package fourthargument.eris.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.services.MessageService;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.UserException;

@Controller
@RequiredArgsConstructor
public class RealtimeChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    // ── Messages ──
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO messageDTO) throws ChannelException, UserException {
        // Persister via le service existant (channelId vient du DTO)
        MessageDTO saved = messageService.sendMessage(messageDTO, messageDTO.channelId());

        // Broadcast à tous les abonnés du channel
        messagingTemplate.convertAndSend(
                "/topic/channels/" + messageDTO.channelId(), saved);
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
}