package fourthargument.eris.api.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.UserException;

@Service("serverSecurityService")
@AllArgsConstructor
public class ServerSecurityService {

    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final UserRepository userRepository;

    public boolean isMemberOfServer(Long serverId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserException("No user with this email exist !"));
            Server server = serverRepository.findById(serverId).orElse(null);

            if (server == null || user == null) {
                return false;
            }

            return serverMemberRepository.existsByUserAndServer(user, server);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMemberOfChannel(Long channelId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserException("No user with this email exist !"));
            Channel channel = channelRepository.findById(channelId).orElse(null);
            Server server = channel.getServer();

            if (channel == null || server == null || user == null) {
                return false;
            }

            return serverMemberRepository.existsByUserAndServer(user, server);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isServerAdmin(Long serverId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserException("No user with this email exist !"));
            Server server = serverRepository.findById(serverId).orElse(null);

            if (server == null || user == null) {
                return false;
            }

            ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

            if (serverMember == null) {
                return false;
            }

            String roleName = serverMember.getRole().getName();
            return "OWNER".equals(roleName) || "ADMIN".equals(roleName);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isChannelAdmin(Long channelId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserException("No user with this email exist !"));
            Channel channel = channelRepository.findById(channelId).orElse(null);
            Server server = channel.getServer();

            if (channel == null || server == null || user == null) {
                return false;
            }

            ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

            if (serverMember == null) {
                return false;
            }

            String roleName = serverMember.getRole().getName();
            return "OWNER".equals(roleName) || "ADMIN".equals(roleName);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isServerOwner(Long serverId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserException("No user with this email exist !"));
            Server server = serverRepository.findById(serverId).orElse(null);

            if (server == null || user == null) {
                return false;
            }

            ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

            if (serverMember == null) {
                return false;
            }

            return "OWNER".equals(serverMember.getRole().getName());
        } catch (Exception e) {
            return false;
        }
    }

}