package fourthargument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import fourthargument.eris.api.controllers.RealtimeChatController;
import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.services.MessageService;
import fourthargument.eris.api.services.PrivateMessageService;

@ExtendWith(MockitoExtension.class)
class RealtimeChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private MessageService messageService;
    @Mock
    private PrivateMessageService privateMessageService;

    private RealtimeChatController controller;

    @BeforeEach
    void setUp() {
        controller = new RealtimeChatController(messagingTemplate, messageService, privateMessageService);
    }

    @Test
    void processMessage_success() throws Exception {
        MessageDTO inputDTO = new MessageDTO(null, 1L, null, "Hello", 5L, null);
        MessageDTO savedDTO = new MessageDTO(1L, 1L, "user@test.com", "Hello", 5L, LocalDateTime.now());

        when(messageService.sendMessage(inputDTO, 5L)).thenReturn(savedDTO);

        controller.processMessage(inputDTO);

        verify(messageService).sendMessage(inputDTO, 5L);
        verify(messagingTemplate).convertAndSend("/topic/channels/5", savedDTO);
    }

    @Test
    void handleTyping_broadcastsToChannel() {
        RealtimeChatController.TypingPayload payload = new RealtimeChatController.TypingPayload(1L, "testuser", 5L,
                true);

        controller.handleTyping(payload);

        verify(messagingTemplate).convertAndSend("/topic/channels/5/typing", payload);
    }

    @Test
    void typingPayload_record() {
        RealtimeChatController.TypingPayload payload = new RealtimeChatController.TypingPayload(1L, "user", 5L, true);

        assertEquals(1L, payload.userId());
        assertEquals("user", payload.username());
        assertEquals(5L, payload.channelId());
        assertTrue(payload.typing());
    }
}
