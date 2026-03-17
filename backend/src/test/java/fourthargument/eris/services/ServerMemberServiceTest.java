package fourthargument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import fourthargument.eris.api.dto.ServerMemberDTO;
import fourthargument.eris.api.dto.request.UpdateMemberRoleRequestDTO;
import fourthargument.eris.api.mapper.ServerMemberMapper;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.api.services.ServerService;
import fourthargument.eris.api.services.ServerMemberService;
import fourthargument.eris.api.services.UserService;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;

@ExtendWith(MockitoExtension.class)
class ServerMemberServiceTest {

    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private ServerMemberMapper serverMemberMapper;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ServerService serverService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ServerMemberService serverMemberService;

    private User user;
    private Server server;
    private Role role;
    private ServerMember serverMember;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setUsername("user");

        server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(user);

        role = new Role();
        role.setId(1L);
        role.setName("MEMBER");

        serverMember = new ServerMember(user, server, role);
        serverMember.setId(1L); // <--- Ajoute cette ligne !
    }

    // ── createServerMember ──
    @Test
    void createServerMember_success() throws Exception {
        // On configure le mock pour qu'il ne renvoie pas 'already exists'
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        // On simule la sauvegarde en BDD qui génère un ID
        when(serverMemberRepository.save(any(ServerMember.class))).thenAnswer(invocation -> {
            ServerMember member = invocation.getArgument(0);
            member.setId(1L); // On force l'ID pour éviter la NPE plus loin
            return member;
        });

        serverMemberService.createServerMember(server, user, role);

        verify(serverMemberRepository).save(any(ServerMember.class));
        // On vérifie aussi que le message a été envoyé avec le bon ID
        verify(messagingTemplate).convertAndSend(eq("/topic/server_member/1"), any(Object.class));
    }

    @Test
    void createServerMember_alreadyExists() {
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        assertThrows(ServerMemberException.class,
                () -> serverMemberService.createServerMember(server, user, role));
    }

    // ── deleteServerMember ──

    @Test
    void deleteServerMember_success() throws Exception {
        when(userService.getUserEntityByEmail(user.getEmail())).thenReturn(user);
        when(serverRepository.findById(server.getId())).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        serverMemberService.deleteServerMember(user.getEmail(), server.getId());

        verify(serverMemberRepository).delete(serverMember);

        verify(messagingTemplate).convertAndSend(eq("/topic/server_member/1"), (Object) any(Map.class));
    }

    @Test
    void deleteServerMember_notFound() throws Exception {
        when(userService.getUserEntityByEmail(user.getEmail())).thenReturn(user);
        when(serverRepository.findById(server.getId())).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertThrows(ServerMemberException.class,
                () -> serverMemberService.deleteServerMember(user.getEmail(), server.getId()));
    }

    // ── getMembersByServerId ──

    @Test
    void getMembersByServerId_success() throws Exception {
        ServerMemberDTO dto = new ServerMemberDTO();
        dto.setUserId(1L);

        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByServer(server)).thenReturn(List.of(serverMember));
        when(serverMemberMapper.toDTO(serverMember)).thenReturn(dto);

        List<ServerMemberDTO> result = serverMemberService.getMembersByServerId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getMembersByServerId_serverNotFound() {
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> serverMemberService.getMembersByServerId(99L));
    }

    // ── updateServerMember ──

    @Test
    void updateServerMember_success() throws Exception {
        UpdateMemberRoleRequestDTO dto = new UpdateMemberRoleRequestDTO();
        dto.setRoleId(2L);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        when(serverRepository.findById(server.getId())).thenReturn(Optional.of(server));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        serverMemberService.updateServerMember(user.getEmail(), server.getId(), user.getId(), dto);

        assertEquals("MEMBER", serverMember.getRole().getName());
        verify(serverMemberRepository).save(serverMember);
    }

    @Test
    void updateServerMember_notFound() {
        UpdateMemberRoleRequestDTO dto = new UpdateMemberRoleRequestDTO();
        dto.setRoleId(1L);
        when(serverRepository.findById(server.getId())).thenReturn(Optional.of(server));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertThrows(ServerMemberException.class,
                () -> serverMemberService.updateServerMember(user.getEmail(), server.getId(), user.getId(), dto));
    }
}
