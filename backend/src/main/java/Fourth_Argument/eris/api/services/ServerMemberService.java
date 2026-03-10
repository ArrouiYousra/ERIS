package Fourth_Argument.eris.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;

@Service
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;
    private final ServerRepository serverRepository;

    public ServerMemberService(
            ServerMemberRepository serverMemberRepository,
            ServerRepository serverRepository,
            ServerMemberMapper mapper,
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.serverMemberRepository = serverMemberRepository;
        this.serverRepository = serverRepository;
        this.serverMemberMapper = mapper;

    }

    public void createServerMember(Server server, User user, Role role) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember != null) {
            throw new ServerMemberException("ServerMember already exists");
        }

        serverMember = new ServerMember(user, server, role);
        serverMemberRepository.save(serverMember);
    }

    public void deleteServerMember(Server server, User user) throws ServerMemberException {
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
