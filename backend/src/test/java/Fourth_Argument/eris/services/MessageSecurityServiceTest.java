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
import fourth_argument.eris.api.model.Message;
import fourth_argument.eris.api.model.Role;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.ServerMember;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.MessageRepository;
import fourth_argument.eris.api.repository.ServerMemberRepository;
import fourth_argument.eris.api.services.MessageSecurityService;
import fourth_argument.eris.api.services.UserService;
import fourth_argument.eris.exceptions.UserException;

@ExtendWith(MockitoExtension.class)
class MessageSecurityServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private MessageSecurityService messageSecurityService;

    private User user;
    private User otherUser;
    private Message message;
    private Server server;
    private Channel channel;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(user);

        channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setServer(server);

        message = new Message();
        message.setId(1L);
        message.setSender(user);
        message.setContent("Hello");
        message.setChannel(channel);
    }

    @Test
    void canDeleteMessage_senderCanDelete() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertTrue(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_messageNull() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(messageSecurityService.canDeleteMessage(99L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_adminCanDelete() throws Exception {
        message.setSender(otherUser); // message from another user

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        ServerMember adminMember = new ServerMember();
        adminMember.setUser(user);
        adminMember.setRole(adminRole);

        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(adminMember);

        assertTrue(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_ownerCanDelete() throws Exception {
        message.setSender(otherUser);

        Role ownerRole = new Role();
        ownerRole.setName("OWNER");

        ServerMember ownerMember = new ServerMember();
        ownerMember.setUser(user);
        ownerMember.setRole(ownerRole);

        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(ownerMember);

        assertTrue(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_memberCannotDelete() throws Exception {
        message.setSender(otherUser);

        Role memberRole = new Role();
        memberRole.setName("MEMBER");

        ServerMember member = new ServerMember();
        member.setUser(user);
        member.setRole(memberRole);

        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(member);

        assertFalse(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_notAMember() throws Exception {
        message.setSender(otherUser);

        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(serverMemberRepository.findServerMemberByUserAndServer(user, server)).thenReturn(null);

        assertFalse(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }

    @Test
    void canDeleteMessage_exceptionReturnsfalse() throws Exception {
        when(userService.getUserEntityByEmail("user@example.com")).thenThrow(new UserException("User not found"));

        assertFalse(messageSecurityService.canDeleteMessage(1L, "user@example.com"));
    }
}
