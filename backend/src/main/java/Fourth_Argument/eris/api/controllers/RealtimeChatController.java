package Fourth_Argument.eris.api.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.services.ChannelService;
import Fourth_Argument.eris.services.MessageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RealtimeChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/{channelId}")
    public void processMessage(
            @Payload MessageDTO messageDTO,
            @PathVariable Long channelId) {

        // 1️⃣ Persist message (same logic as REST)
        MessageDTO savedMessage = messageService.sendMessage(messageDTO, channelId);

        // 2️⃣ Broadcast to all subscribers of the channel
        messagingTemplate.convertAndSend(
                "/topic/channels/" + channelId,
                savedMessage);
    }
}
