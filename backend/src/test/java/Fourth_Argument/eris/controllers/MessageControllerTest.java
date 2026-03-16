package Fourth_Argument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import Fourth_Argument.eris.api.controllers.MessageController;
import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.services.MessageService;
import Fourth_Argument.eris.exceptions.MessageException;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    private MessageController controller;

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        // MessageController uses @RequiredArgsConstructor, create manually
        controller = new MessageController(messageService);

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
        when(messageService.getMessageHistory(1L)).thenReturn(List.of(messageDTO));

        ResponseEntity<List<MessageDTO>> response = controller.getMessageHistory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getMessageHistory_channelNotFound() {
        when(messageService.getMessageHistory(99L)).thenThrow(new MessageException("This channel is not found !"));

        assertThrows(MessageException.class,
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
