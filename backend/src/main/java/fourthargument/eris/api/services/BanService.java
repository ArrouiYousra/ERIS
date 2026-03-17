package fourthargument.eris.api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.model.BannedMember;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.BannedMemberRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;
import fourthargument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BanService {

    private final BannedMemberRepository bannedMemberRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public BannedMember banUser(final Long serverId, final Long userId, final String bannedByEmail,
            final String reason, final Integer durationInHours) throws UserException, ServerException {

        final User user = userService.getUserById(userId);
        final Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));
        final User bannedBy = userService.getUserEntityByEmail(bannedByEmail);

        // Vérifier que l'utilisateur n'est pas déjà banni
        if (bannedMemberRepository.isCurrentlyBanned(user, server)) {
            throw new ServerException("User is already banned from this server");
        }

        // Supprimer le membre du serveur
        final ServerMember member = serverMemberRepository.findServerMemberByUserAndServer(user, server);
        if (member != null) {
            serverMemberRepository.delete(member);
        }

        // Créer le ban
        final BannedMember ban = new BannedMember();
        ban.setUser(user);
        ban.setServer(server);
        ban.setBannedBy(bannedBy);
        ban.setReason(reason);
        ban.setBannedAt(LocalDateTime.now());

        if (durationInHours != null) {
            ban.setExpiresAt(LocalDateTime.now().plusHours(durationInHours));
        }

        final BannedMember saved = bannedMemberRepository.save(ban);

        // Broadcast
        messagingTemplate.convertAndSend("/topic/servers/" + serverId + "/members",
                (Object) Map.of("type", "MEMBER_BANNED", "userId", userId));
        messagingTemplate.convertAndSend("/topic/servers",
                (Object) Map.of("type", "UPDATED", "serverId", serverId));

        return saved;
    }

    public void unbanUser(final Long serverId, final Long userId, final String unbannedByEmail)
            throws UserException, ServerException {

        final User user = userService.getUserById(userId);
        final Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        final BannedMember ban = bannedMemberRepository.findByUserAndServer(user, server)
                .orElseThrow(() -> new ServerException("User is not banned from this server"));

        bannedMemberRepository.delete(ban);

        // Broadcast
        messagingTemplate.convertAndSend("/topic/servers/" + serverId + "/members",
                (Object) Map.of("type", "MEMBER_UNBANNED", "userId", userId));
    }

    public List<BannedMember> getBannedMembers(final Long serverId) throws ServerException {
        final Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));
        return bannedMemberRepository.findByServer(server);
    }

    public boolean isUserBanned(final User user, final Server server) {
        return bannedMemberRepository.isCurrentlyBanned(user, server);
    }
}
