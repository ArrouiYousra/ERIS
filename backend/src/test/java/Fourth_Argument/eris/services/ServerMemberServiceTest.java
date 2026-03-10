package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;

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
    }

    // ── createServerMember ──

    @Test
    void createServerMember_success() throws Exception {
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        serverMemberService.createServerMember(server, user, role);

        verify(serverMemberRepository).save(any(ServerMember.class));
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
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        serverMemberService.deleteServerMember(user.getEmail(), server.getId());

        verify(serverMemberRepository).delete(serverMember);
    }

    @Test
    void deleteServerMember_notFound() {
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
        Role newRole = new Role();
        newRole.setName("ADMIN");

        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        serverMemberService.updateServerMember(server, user, newRole);

        assertEquals("ADMIN", serverMember.getRole().getName());
        verify(serverMemberRepository).save(serverMember);
    }

    @Test
    void updateServerMember_notFound() {
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertThrows(ServerMemberException.class,
                () -> serverMemberService.updateServerMember(server, user, role));
    }
}
