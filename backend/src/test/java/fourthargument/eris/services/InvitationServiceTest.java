package fourthargument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import fourthargument.eris.api.dto.InvitationDTO;
import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.dto.response.JoinInviteResponseDTO;
import fourthargument.eris.api.mapper.InvitationMapper;
import fourthargument.eris.api.mapper.MessageMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Invitation;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.InvitationRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.api.services.InvitationService;
import fourthargument.eris.api.services.ServerMemberService;
import fourthargument.eris.api.services.UserService;
import fourthargument.eris.exceptions.RoleException;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;
import fourthargument.eris.exceptions.UserException;

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
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageMapper messageMapper; // Celui qui causait la NPE

    @Mock
    private SimpMessagingTemplate messagingTemplate; // Indispensable pour le convertAndSend

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
        // 1. Setup Invitation & Role
        Invitation invite = new Invitation();
        invite.setCode("abc12345");
        invite.setServer(server); // Assure-toi que server.getName() est "Test Server"
        invite.setExpiresAt(LocalDateTime.now().plusDays(1));

        Role memberRole = new Role();
        memberRole.setName("MEMBER");

        Channel channel = new Channel();
        channel.setId(123L); // Pour éviter /topic/channels/null

        // Configurer le mapper pour qu'il ne renvoie pas null
        MessageDTO mockDto = new MessageDTO(
                1L, // id
                1L, // senderId
                "Bienvenue !", // content
                "SystemBot", // senderName
                123L, // channelId
                LocalDateTime.now() // timestamp (ou autre String selon ton DTO)
        );

        when(messageMapper.toDTO(any(Message.class))).thenReturn(mockDto);

        // 2. Mock du Bot (Le coupable !)
        User bot = new User();
        bot.setUsername("SystemBot");
        when(userRepository.findByUsername("SystemBot")).thenReturn(Optional.of(bot));

        // 3. Mocks habituels
        when(userService.getUserEntityByEmail("joiner@example.com")).thenReturn(user);
        when(invitationRepository.findByCode("abc12345")).thenReturn(Optional.of(invite));
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.of(memberRole));

        // 4. Mocks pour la fin de la méthode (Messages & Channels)
        when(channelRepository.getChannelsByServer(server)).thenReturn(List.of(channel));
        // Pas besoin de mapper le retour de save, mais Mockito aime bien avoir les
        // mocks
        when(messageRepository.save(any(Message.class))).thenReturn(new Message());

        // Act
        JoinInviteResponseDTO result = invitationService.joinServerWithInvite("joiner@example.com", "abc12345");

        // Assert
        assertNotNull(result);
        assertEquals("Test Server", result.getServerName());
        verify(serverMemberService).createServerMember(server, user, memberRole);

        verify(messagingTemplate).convertAndSend(anyString(), any(MessageDTO.class));
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

        assertThrows(UserException.class,
                () -> invitationService.joinServerWithInvite("joiner@example.com", "abc12345"));
    }
}
