package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Fourth_Argument.eris.api.dto.InvitationDTO;
import Fourth_Argument.eris.api.dto.response.JoinInviteResponseDTO;
import Fourth_Argument.eris.api.mapper.InvitationMapper;
import Fourth_Argument.eris.api.model.Invitation;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.InvitationRepository;
import Fourth_Argument.eris.api.repository.RoleRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.RoleException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private InvitationMapper invitationMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private ServerMemberService serverMemberService;
    @Mock
    private UserService userService;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private InvitationService invitationService;

    private User user;
    private Server server;
    private Role ownerRole;
    private ServerMember ownerMember;
    private InvitationDTO invitationDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("owner@example.com");
        user.setUsername("owner");

        server = new Server();
        server.setId(1L);
        server.setName("Test Server");
        server.setOwner(user);

        ownerRole = new Role();
        ownerRole.setName("OWNER");

        ownerMember = new ServerMember();
        ownerMember.setUser(user);
        ownerMember.setServer(server);
        ownerMember.setRole(ownerRole);

        invitationDTO = new InvitationDTO();
        invitationDTO.setCode("abc12345");
        invitationDTO.setExpiresAt(LocalDateTime.now().plusDays(1));
    }

    // ── generateCode ──

    @Test
    void generateCode_returns8Chars() {
        String code = invitationService.generateCode();

        assertNotNull(code);
        assertEquals(8, code.length());
        assertFalse(code.contains("-"));
    }

    // ── createInvite ──

    @Test
    void createInvite_success_noExisting() throws Exception {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByUserAndServer(user, server)).thenReturn(Optional.of(ownerMember));
        when(invitationRepository.findFirstByServerOrderByCreatedAtDesc(server)).thenReturn(Optional.empty());
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invitationMapper.toDTO(any(Invitation.class))).thenReturn(invitationDTO);

        InvitationDTO result = invitationService.createInvite("owner@example.com", 1L);

        assertNotNull(result);
        verify(invitationRepository).save(any(Invitation.class));
    }

    @Test
    void createInvite_existingNonExpired() throws Exception {
        Invitation existing = new Invitation();
        existing.setCode("existing1");
        existing.setExpiresAt(LocalDateTime.now().plusHours(12));

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByUserAndServer(user, server)).thenReturn(Optional.of(ownerMember));
        when(invitationRepository.findFirstByServerOrderByCreatedAtDesc(server)).thenReturn(Optional.of(existing));
        when(invitationMapper.toDTO(existing)).thenReturn(invitationDTO);

        InvitationDTO result = invitationService.createInvite("owner@example.com", 1L);

        assertNotNull(result);
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void createInvite_userNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserException.class,
                () -> invitationService.createInvite("unknown@example.com", 1L));
    }

    @Test
    void createInvite_serverNotFound() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> invitationService.createInvite("owner@example.com", 99L));
    }

    @Test
    void createInvite_notAMember() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByUserAndServer(user, server)).thenReturn(Optional.empty());

        assertThrows(ServerMemberException.class,
                () -> invitationService.createInvite("owner@example.com", 1L));
    }

    @Test
    void createInvite_notAdmin() {
        Role memberRole = new Role();
        memberRole.setName("MEMBER");

        ServerMember regularMember = new ServerMember();
        regularMember.setUser(user);
        regularMember.setRole(memberRole);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByUserAndServer(user, server)).thenReturn(Optional.of(regularMember));

        assertThrows(RoleException.class,
                () -> invitationService.createInvite("owner@example.com", 1L));
    }

    // ── joinServerWithInvite ──

    @Test
    void joinServerWithInvite_success() throws Exception {
        Invitation invite = new Invitation();
        invite.setCode("abc12345");
        invite.setServer(server);
        invite.setExpiresAt(LocalDateTime.now().plusDays(1));

        Role memberRole = new Role();
        memberRole.setName("MEMBER");

        when(userService.getUserEntityByEmail("joiner@example.com")).thenReturn(user);
        when(invitationRepository.findByCode("abc12345")).thenReturn(Optional.of(invite));
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.of(memberRole));

        JoinInviteResponseDTO result = invitationService.joinServerWithInvite("joiner@example.com", "abc12345");

        assertNotNull(result);
        assertEquals("Test Server", result.getServerName());
        verify(serverMemberService).createServerMember(server, user, memberRole);
    }

    @Test
    void joinServerWithInvite_invalidCode() throws Exception {
        when(userService.getUserEntityByEmail("joiner@example.com")).thenReturn(user);
        when(invitationRepository.findByCode("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> invitationService.joinServerWithInvite("joiner@example.com", "invalid"));
    }

    @Test
    void joinServerWithInvite_expired() throws Exception {
        Invitation invite = new Invitation();
        invite.setCode("expired1");
        invite.setServer(server);
        invite.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(userService.getUserEntityByEmail("joiner@example.com")).thenReturn(user);
        when(invitationRepository.findByCode("expired1")).thenReturn(Optional.of(invite));

        assertThrows(RuntimeException.class,
                () -> invitationService.joinServerWithInvite("joiner@example.com", "expired1"));
    }

    @Test
    void joinServerWithInvite_roleNotFound() throws Exception {
        Invitation invite = new Invitation();
        invite.setCode("abc12345");
        invite.setServer(server);
        invite.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(userService.getUserEntityByEmail("joiner@example.com")).thenReturn(user);
        when(invitationRepository.findByCode("abc12345")).thenReturn(Optional.of(invite));
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> invitationService.joinServerWithInvite("joiner@example.com", "abc12345"));
    }
}
