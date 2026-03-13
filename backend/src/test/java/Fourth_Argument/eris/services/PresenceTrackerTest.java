package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.util.ReflectionTestUtils;

import fourth_argument.eris.api.model.Role;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.ServerMember;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.ServerMemberRepository;
import fourth_argument.eris.api.repository.ServerRepository;
import fourth_argument.eris.api.services.PresenceTracker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class PresenceTrackerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private ServerRepository serverRepository;

    private PresenceTracker presenceTracker;

    private User user;
    private Server server;
    private ServerMember member;

    @BeforeEach
    void setUp() {
        presenceTracker = new PresenceTracker(messagingTemplate, serverMemberRepository, serverRepository);

        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(user);

        Role role = new Role();
        role.setName("MEMBER");

        member = new ServerMember(user, server, role);
    }

    @Test
    void registerUser_success() {
        PresenceTracker.PresencePayload payload = new PresenceTracker.PresencePayload(1L);

        StompHeaderAccessor accessor = StompHeaderAccessor
                .create(org.springframework.messaging.simp.stomp.StompCommand.SEND);
        accessor.setSessionId("session1");

        when(serverMemberRepository.findServerMemberByUserId(1L)).thenReturn(List.of(member));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByServer(server)).thenReturn(List.of(member));

        presenceTracker.registerUser(payload, accessor);

        verify(messagingTemplate).convertAndSend(eq("/topic/servers/1/presence"), any(Set.class));
    }

    @Test
    void registerUser_nullSessionId() {
        PresenceTracker.PresencePayload payload = new PresenceTracker.PresencePayload(1L);
        StompHeaderAccessor accessor = StompHeaderAccessor
                .create(org.springframework.messaging.simp.stomp.StompCommand.SEND);
        // Don't set sessionId - it will be null

        presenceTracker.registerUser(payload, accessor);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void registerUser_nullUserId() {
        PresenceTracker.PresencePayload payload = new PresenceTracker.PresencePayload(null);
        StompHeaderAccessor accessor = StompHeaderAccessor
                .create(org.springframework.messaging.simp.stomp.StompCommand.SEND);
        accessor.setSessionId("session1");

        presenceTracker.registerUser(payload, accessor);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void getOnlineUserIdsForServer_noServer() {
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        Set<Long> online = presenceTracker.getOnlineUserIdsForServer(99L);

        assertTrue(online.isEmpty());
    }

    @Test
    void getOnlineUserIdsForServer_noOnlineUsers() {
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByServer(server)).thenReturn(List.of(member));

        Set<Long> online = presenceTracker.getOnlineUserIdsForServer(1L);

        assertTrue(online.isEmpty());
    }

    @Test
    void getOnlineUserIdsForServer_withOnlineUser() {
        // Simulate a connected user by manually adding to the internal maps
        Map<Long, Set<String>> userSessions = (Map<Long, Set<String>>) ReflectionTestUtils.getField(presenceTracker,
                "userSessions");
        Set<String> sessions = ConcurrentHashMap.newKeySet();
        sessions.add("session1");
        userSessions.put(1L, sessions);

        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMemberRepository.findByServer(server)).thenReturn(List.of(member));

        Set<Long> online = presenceTracker.getOnlineUserIdsForServer(1L);

        assertEquals(1, online.size());
        assertTrue(online.contains(1L));
    }

    @Test
    void presencePayload_record() {
        PresenceTracker.PresencePayload payload = new PresenceTracker.PresencePayload(42L);
        assertEquals(42L, payload.userId());
    }
}
