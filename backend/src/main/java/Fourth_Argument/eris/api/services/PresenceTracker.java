package Fourth_Argument.eris.api.services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PresenceTracker {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;

    // sessionId → userId
    private final Map<String, Long> sessionToUser = new ConcurrentHashMap<>();
    // userId → set of sessionIds (un user peut avoir plusieurs onglets)
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    /**
     * Appelé par le front juste après la connexion STOMP.
     * Le front envoie { userId: 123 } sur /app/presence.connect
     */
    @MessageMapping("/presence.connect")
    public void registerUser(@Payload PresencePayload payload, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        Long userId = payload.userId();

        if (sessionId == null || userId == null)
            return;

        sessionToUser.put(sessionId, userId);
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);

        System.out.println("✅ Presence: user " + userId + " connected (session " + sessionId + ")");

        broadcastPresenceForUser(userId);
    }

    /**
     * Détecte automatiquement les déconnexions WebSocket.
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        Long userId = sessionToUser.remove(sessionId);
        if (userId == null)
            return;

        Set<String> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }

        System.out.println("❌ Presence: user " + userId + " disconnected (session " + sessionId + ")");

        broadcastPresenceForUser(userId);
    }

    /**
     * Broadcast la liste des user IDs connectés pour chaque serveur du user.
     */
    private void broadcastPresenceForUser(Long userId) {
        try {
            // Trouver tous les serveurs de ce user via ses memberships
            List<ServerMember> memberships = serverMemberRepository.findServerMemberByUserId(userId);

            // Extraire les serverIds uniques
            Set<Long> serverIds = memberships.stream()
                    .map(m -> m.getServer().getId())
                    .collect(Collectors.toSet());

            for (Long serverId : serverIds) {
                Set<Long> onlineIds = getOnlineUserIdsForServer(serverId);
                messagingTemplate.convertAndSend(
                        "/topic/servers/" + serverId + "/presence", onlineIds);
            }
        } catch (Exception e) {
            System.err.println("Presence broadcast error: " + e.getMessage());
        }
    }

    /**
     * Retourne les IDs des users connectés pour un serveur donné.
     */
    public Set<Long> getOnlineUserIdsForServer(Long serverId) {
        Set<Long> online = ConcurrentHashMap.newKeySet();
        try {
            Server server = serverRepository.findById(serverId).orElse(null);
            if (server == null)
                return online;

            List<ServerMember> members = serverMemberRepository.findByServer(server);
            for (ServerMember member : members) {
                Long memberId = member.getUser().getId();
                if (userSessions.containsKey(memberId) && !userSessions.get(memberId).isEmpty()) {
                    online.add(memberId);
                }
            }
        } catch (Exception e) {
            // Silently fail
        }
        return online;
    }

    public record PresencePayload(Long userId) {
    }
}