package fourthargument.eris.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.ServerMemberDTO;
import fourthargument.eris.api.mapper.ServerMemberMapper;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;
import fourthargument.eris.exceptions.UserException;

@Service
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;
    private final ServerRepository serverRepository;
    private final UserService userService;

    public ServerMemberService(
            ServerMemberRepository serverMemberRepository,
            ServerRepository serverRepository,
            ServerMemberMapper mapper, UserService userService,
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.serverMemberRepository = serverMemberRepository;
        this.serverRepository = serverRepository;
        this.serverMemberMapper = mapper;
        this.userService = userService;
    }

    public void createServerMember(Server server, User user, Role role) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember != null) {
            throw new ServerMemberException("ServerMember already exists");
        }

        serverMember = new ServerMember(user, server, role);
        serverMemberRepository.save(serverMember);
    }

    public void deleteServerMember(String email, Long serverId)
            throws ServerException, ServerMemberException, UserException {

        User user = userService.getUserEntityByEmail(email);
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        serverMemberRepository.delete(serverMember);
    }

    public List<ServerMemberDTO> getMembersByServerId(Long serverId) throws ServerException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        List<ServerMember> members = serverMemberRepository.findByServer(server);

        return members.stream()
                .map(serverMemberMapper::toDTO)
                .toList();
    }

    public void updateServerMember(Server server, User user, Role role) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        // tu ne peux pas changer le tien, il faut toujours un owner
        // si tu changes en owner, le tien change en admin
        serverMember.setRole(role);
        serverMemberRepository.save(serverMember);
    }
}
