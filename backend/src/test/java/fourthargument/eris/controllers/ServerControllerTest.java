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
import org.springframework.security.core.userdetails.UserDetails;

import fourthargument.eris.api.controllers.ServerController;
import fourthargument.eris.api.dto.InvitationDTO;
import fourthargument.eris.api.dto.ServerDTO;
import fourthargument.eris.api.dto.ServerMemberDTO;
import fourthargument.eris.api.dto.request.JoinInviteRequestDTO;
import fourthargument.eris.api.dto.response.JoinInviteResponseDTO;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.InvitationService;
import fourthargument.eris.api.services.ServerMemberService;
import fourthargument.eris.api.services.ServerService;
import fourthargument.eris.api.services.UserService;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ServerControllerTest {

    @Mock
    private ServerService serverService;
    @Mock
    private ServerMemberService serverMemberService;
    @Mock
    private UserService userService;
    @Mock
    private InvitationService invitationService;

    @InjectMocks
    private ServerController controller;

    private User owner;
    private ServerDTO serverDTO;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setUsername("owner");

        serverDTO = new ServerDTO();
        serverDTO.setId(1L);
        serverDTO.setName("Test Server");
        serverDTO.setOwnerId(1L);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("owner@example.com");
    }

    @Test
    void createServer_success() throws Exception {
        when(userService.getUserEntityByEmail("owner@example.com")).thenReturn(owner);
        when(serverService.createServer(any(ServerDTO.class), eq(owner))).thenReturn(serverDTO);

        ServerDTO inputDTO = new ServerDTO();
        inputDTO.setName("Test Server");
        ResponseEntity<ServerDTO> response = controller.createServer(inputDTO, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Server", response.getBody().getName());
    }

    @Test
    void getUserServers_success() throws Exception {
        when(userService.getUserEntityByEmail("owner@example.com")).thenReturn(owner);
        when(serverService.getServersByUser(owner)).thenReturn(List.of(serverDTO));

        ResponseEntity<List<ServerDTO>> response = controller.getUserServers(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getServer_success() throws Exception {
        when(serverService.getServerById(1L)).thenReturn(serverDTO);

        ResponseEntity<ServerDTO> response = controller.getServer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Server", response.getBody().getName());
    }

    @Test
    void updateServer_success() throws Exception {
        doNothing().when(serverService).updateServer(1L, serverDTO);

        ResponseEntity<String> response = controller.updateServer(1L, serverDTO, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Server updated", response.getBody());
    }

    @Test
    void getServerMembers_success() throws Exception {
        ServerMemberDTO memberDTO = new ServerMemberDTO();
        memberDTO.setUserId(1L);
        when(serverMemberService.getMembersByServerId(1L)).thenReturn(List.of(memberDTO));

        ResponseEntity<List<ServerMemberDTO>> response = controller.getServerMembers(1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createInvitation_success() throws Exception {
        InvitationDTO invDTO = new InvitationDTO();
        invDTO.setCode("abc12345");
        when(invitationService.createInvite("owner@example.com", 1L)).thenReturn(invDTO);

        ResponseEntity<InvitationDTO> response = controller.createInvitation(userDetails, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc12345", response.getBody().getCode());
    }

    @Test
    void joinServer_success() throws Exception {
        JoinInviteRequestDTO request = new JoinInviteRequestDTO();
        request.setCode("abc12345");

        JoinInviteResponseDTO responseDTO = new JoinInviteResponseDTO();
        responseDTO.setServerName("Test Server");
        responseDTO.setServerId(1L);

        when(invitationService.joinServerWithInvite("owner@example.com", "abc12345")).thenReturn(responseDTO);

        ResponseEntity<JoinInviteResponseDTO> response = controller.joinServer(userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Server", response.getBody().getServerName());
    }
}
