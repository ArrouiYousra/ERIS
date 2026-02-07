package Fourth_Argument.eris.services;

import java.net.ContentHandlerFactory;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;

@Service
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;
    private final ServerRepository serverRepository;
    private final UserService userService;

    public ServerMemberService(
            ServerMemberRepository serverMemberRepository,
            ServerRepository serverRepository,
            ServerMemberMapper mapper, UserService userService) {
        this.serverMemberRepository = serverMemberRepository;
        this.serverRepository = serverRepository;
        this.serverMemberMapper = mapper;
        this.userService = userService;
    }

<<<<<<< HEAD
    public void createServerMember(Server server, User user, Role role) {

=======
    public void createServerMember(Server server, User user, Role role) throws ServerMemberException {
>>>>>>> b030507a6d5155a219845ef19fde73bc64deb56d
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(server, user);

        if (serverMember != null) {
            throw new ServerMemberException("ServerMember already exists");
        }

        serverMember = new ServerMember(user, server, role);
        serverMemberRepository.save(serverMember);
    }

<<<<<<< HEAD
    public void deleteServerMember(Server server, User user) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User loggedUser = userService.getUserEntityByEmail(email);

        ServerMember serverAuthMember = serverMemberRepository.findServerMemberByUserAndServer(server, loggedUser);

        if (serverAuthMember.getRole().getName() != "ADMIN") {
            throw new RuntimeException("You don't have the rights do to it !");
        }

=======
    public void deleteServerMember(Server server, User user) throws ServerMemberException {
>>>>>>> b030507a6d5155a219845ef19fde73bc64deb56d
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(server, user);

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
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(server, user);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        // tu ne peux pas changer le tien, il faut toujours un owner
        // si tu changes en owner, le tien change en admin
        serverMember.setRole(role);
        serverMemberRepository.save(serverMember);
    }
}
