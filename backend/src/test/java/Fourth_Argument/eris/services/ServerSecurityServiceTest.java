package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Role;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.ServerMember;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.ChannelRepository;
import fourth_argument.eris.api.repository.ServerMemberRepository;
import fourth_argument.eris.api.repository.ServerRepository;
import fourth_argument.eris.api.services.ServerSecurityService;
import fourth_argument.eris.api.services.UserService;
import fourth_argument.eris.exceptions.UserException;

@ExtendWith(MockitoExtension.class)
class ServerSecurityServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ServerSecurityService serverSecurityService;

    private User user;
    private Server server;
    private Channel channel;
    private ServerMember adminMember;
    private ServerMember ownerMember;
    private ServerMember regularMember;

    @BeforeEach
    void setUp() throws Exception {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(user);

        channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setServer(server);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminMember = new ServerMember();
        adminMember.setUser(user);
        adminMember.setRole(adminRole);

        Role ownerRole = new Role();
        ownerRole.setName("OWNER");
        ownerMember = new ServerMember();
        ownerMember.setUser(user);
        ownerMember.setRole(ownerRole);

        Role memberRole = new Role();
        memberRole.setName("MEMBER");
        regularMember = new ServerMember();
        regularMember.setUser(user);
        regularMember.setRole(memberRole);
    }

    // ── isMemberOfServer ──

    @Test
    void isMemberOfServer_true() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.existsByUserAndServer(user, server)).thenReturn(true);

        assertTrue(serverSecurityService.isMemberOfServer(1L, "user@example.com"));
    }

    @Test
    void isMemberOfServer_false() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.existsByUserAndServer(user, server)).thenReturn(false);

        assertFalse(serverSecurityService.isMemberOfServer(1L, "user@example.com"));
    }

    @Test
    void isMemberOfServer_serverNull() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(serverSecurityService.isMemberOfServer(99L, "user@example.com"));
    }

    @Test
    void isMemberOfServer_exception() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenThrow(new UserException("not found"));

        assertFalse(serverSecurityService.isMemberOfServer(1L, "user@example.com"));
    }

    // ── isMemberOfChannel ──

    @Test
    void isMemberOfChannel_true() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(serverMemberRepository.existsByUserAndServer(user, server)).thenReturn(true);

        assertTrue(serverSecurityService.isMemberOfChannel(1L, "user@example.com"));
    }

    @Test
    void isMemberOfChannel_channelNull() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        // channel is null -> NullPointerException caught -> returns false
        assertFalse(serverSecurityService.isMemberOfChannel(99L, "user@example.com"));
    }

    // ── isServerAdmin ──

    @Test
    void isServerAdmin_admin() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(adminMember);

        assertTrue(serverSecurityService.isServerAdmin(1L, "user@example.com"));
    }

    @Test
    void isServerAdmin_owner() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(ownerMember);

        assertTrue(serverSecurityService.isServerAdmin(1L, "user@example.com"));
    }

    @Test
    void isServerAdmin_regularMember() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(regularMember);

        assertFalse(serverSecurityService.isServerAdmin(1L, "user@example.com"));
    }

    @Test
    void isServerAdmin_notMember() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertFalse(serverSecurityService.isServerAdmin(1L, "user@example.com"));
    }

    @Test
    void isServerAdmin_serverNull() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(serverSecurityService.isServerAdmin(99L, "user@example.com"));
    }

    // ── isChannelAdmin ──

    @Test
    void isChannelAdmin_admin() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(adminMember);

        assertTrue(serverSecurityService.isChannelAdmin(1L, "user@example.com"));
    }

    @Test
    void isChannelAdmin_notAdmin() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(regularMember);

        assertFalse(serverSecurityService.isChannelAdmin(1L, "user@example.com"));
    }

    @Test
    void isChannelAdmin_memberNull() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertFalse(serverSecurityService.isChannelAdmin(1L, "user@example.com"));
    }

    // ── isServerOwner ──

    @Test
    void isServerOwner_true() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(ownerMember);

        assertTrue(serverSecurityService.isServerOwner(1L, "user@example.com"));
    }

    @Test
    void isServerOwner_false_admin() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(adminMember);

        assertFalse(serverSecurityService.isServerOwner(1L, "user@example.com"));
    }

    @Test
    void isServerOwner_notMember() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertFalse(serverSecurityService.isServerOwner(1L, "user@example.com"));
    }

    @Test
    void isServerOwner_exception() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenThrow(new UserException("not found"));

        assertFalse(serverSecurityService.isServerOwner(1L, "user@example.com"));
    }
}
