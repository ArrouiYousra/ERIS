package fourthargument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fourthargument.eris.api.controllers.MessageController;
import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.services.MessageService;
import fourthargument.eris.exceptions.ChannelException;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;
    @Mock
    private ChannelRepository channelRepository;

    private MessageController controller;

    private Channel channel;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        // MessageController uses @RequiredArgsConstructor, create manually
        controller = new MessageController(messageService);

        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@test.com");

        Server server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(owner);

        channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setServer(server);

        messageDTO = new MessageDTO(1L, 1L, "sender@test.com", "Hello", 1L, null);
    }

    @Test
    void sendMessage_success() throws Exception {
        MessageDTO inputDTO = new MessageDTO(null, 1L, null, "Hello", 1L, null);
        when(messageService.sendMessage(inputDTO, 1L)).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> response = controller.sendMessage(inputDTO, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello", response.getBody().content());
    }

    @Test
    void getMessageHistory_success() throws Exception {
        when(messageService.getMessageHistoryChannel(1L)).thenReturn(List.of(messageDTO));

        ResponseEntity<List<MessageDTO>> response = controller.getMessageHistory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(messageService).getMessageHistoryChannel(1L);
    }

    @Test
    void getMessageHistory_channelNotFound() throws Exception {
        when(messageService.getMessageHistoryChannel(99L))
                .thenThrow(new ChannelException("This channel is not found !"));

        // On vérifie que l'appel au service déclenche bien l'exception
        assertThrows(ChannelException.class,
                () -> controller.getMessageHistory(99L));
    }

    @Test
    void deleteMessage_success() throws Exception {
        doNothing().when(messageService).deleteMessage(1L);

        ResponseEntity<String> response = controller.deleteMessage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("supprimé"));
    }
}
