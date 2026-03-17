package fourthargument.eris.services;

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

    @InjectMocks
    private ServerMemberService serverMemberService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

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
        // On mocke ce que le SERVICE appelle réellement
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));

        // Maintenant "user" ne sera plus null, donc ce stubbing fonctionnera :
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        serverMemberService.deleteServerMember("user@example.com", 1L);

        verify(serverMemberRepository).delete(serverMember);
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
        Long serverId = 1L;
        Long memberId = 9L;
        Long roleId = 1L; // On définit l'ID du rôle

        // 1. Mock du Serveur et du User (pour passer le début de la méthode)
        when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(serverMember);

        // 2. Mock du Rôle (Le coupable de l'erreur actuelle !)
        Role newRole = new Role();
        newRole.setId(roleId);
        newRole.setName("ADMIN");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(newRole));

        UpdateMemberRoleRequestDTO memberRequest = new UpdateMemberRoleRequestDTO();
        memberRequest.setRoleId(roleId);

        // 3. Appel de la méthode
        serverMemberService.updateServerMember("test@example.com", serverId, memberId, memberRequest);

        // 4. Assertions
        verify(serverMemberRepository).save(serverMember);
        // Optionnel : vérifier que le rôle a bien été mis à jour sur l'objet
        assertEquals("ADMIN", serverMember.getRole().getName());
    }

    @Test
    void updateServerMember_notFound() {
        Long serverId = 1L;
        Long memberId = 9L;

        // On mocke le serveur pour passer l'étape 1
        when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));

        // On mocke le user pour passer l'étape 2 (memberId doit matcher l'appel)
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));

        // On simule que le membre n'existe pas pour l'étape 3
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertThrows(ServerMemberException.class,
                () -> serverMemberService.updateServerMember("test@example.com", serverId, memberId,
                        new UpdateMemberRoleRequestDTO()));
    }
}
