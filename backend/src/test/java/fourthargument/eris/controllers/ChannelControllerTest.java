package fourthargument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fourthargument.eris.api.controllers.ChannelController;
import fourthargument.eris.api.dto.ChannelDTO;
import fourthargument.eris.api.services.ChannelService;

@ExtendWith(MockitoExtension.class)
class ChannelControllerTest {

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private ChannelController controller;

    private ChannelDTO channelDTO;

    @BeforeEach
    void setUp() {
        channelDTO = new ChannelDTO(1L, "general", "General chat", false, 1L);
    }

    @Test
    void createChannel_success() throws Exception {
        ChannelDTO inputDTO = new ChannelDTO(null, "general", null, false, 1L);
        when(channelService.createChannel(1L, inputDTO)).thenReturn(channelDTO);

        ResponseEntity<ChannelDTO> response = controller.createChannel(1L, inputDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("general", response.getBody().name());
    }

    @Test
    void getChannelByServer_success() throws Exception {
        when(channelService.getChannelByServer(1L)).thenReturn(List.of(channelDTO));

        ResponseEntity<List<ChannelDTO>> response = controller.getChannelByServer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getChannelById_success() throws Exception {
        when(channelService.findById(1L)).thenReturn(channelDTO);

        ResponseEntity<ChannelDTO> response = controller.getChannelById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("general", response.getBody().name());
    }

    @Test
    void update_success() throws Exception {
        ChannelDTO updateDTO = new ChannelDTO(null, "renamed", null, null, null);
        ChannelDTO updatedDTO = new ChannelDTO(1L, "renamed", "General chat", false, 1L);
        when(channelService.update(updateDTO, 1L)).thenReturn(updatedDTO);

        ResponseEntity<ChannelDTO> response = controller.update(1L, updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("renamed", response.getBody().name());
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(channelService).delete(1L);

        ResponseEntity<String> response = controller.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("supprimé"));
    }
}
