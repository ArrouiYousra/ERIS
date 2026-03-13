package fourth_argument.eris.api.services;

import org.springframework.stereotype.Service;

import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.ServerMember;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.ChannelRepository;
import fourth_argument.eris.api.repository.ServerMemberRepository;
import fourth_argument.eris.api.repository.ServerRepository;
import fourth_argument.eris.api.repository.UserRepository;
import fourth_argument.eris.exceptions.UserException;
import lombok.AllArgsConstructor;

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