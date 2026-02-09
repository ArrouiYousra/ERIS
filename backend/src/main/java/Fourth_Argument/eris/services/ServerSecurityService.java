package Fourth_Argument.eris.services;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;

@Service("serverSecurityService")
public class ServerSecurityService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final UserService userService;

    public ServerSecurityService(ServerRepository serverRepository,
            ServerMemberRepository serverMemberRepository,
            UserService userService) {
        this.serverRepository = serverRepository;
        this.serverMemberRepository = serverMemberRepository;
        this.userService = userService;
    }

    public boolean isMemberOfServer(Long serverId, String userEmail) {
        try {
            User user = userService.getUserEntityByEmail(userEmail);
            Server server = serverRepository.findById(serverId).orElse(null);

            if (server == null || user == null) {
                return false;
            }

            return serverMemberRepository.existsByUserAndServer(user, server);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isServerAdmin(Long serverId, String userEmail) {
        try {
            User user = userService.getUserEntityByEmail(userEmail);
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

    public boolean isServerOwner(Long serverId, String userEmail) {
        try {
            User user = userService.getUserEntityByEmail(userEmail);
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