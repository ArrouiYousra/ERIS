package Fourth_Argument.eris.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.dto.request.UpdateMemberRoleRequestDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.RoleRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.RoleException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;
    private final ServerRepository serverRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ServerService serverService;
    private final SimpMessagingTemplate messagingTemplate;

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
        messagingTemplate.convertAndSend("/topic/server_member", (Object) Map.of("type", "DELETED", "serverMemberId", serverMember.getId()));

    }

    public List<ServerMemberDTO> getMembersByServerId(Long serverId) throws ServerException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        List<ServerMember> members = serverMemberRepository.findByServer(server);

        return members.stream()
                .map(serverMemberMapper::toDTO)
                .toList();
    }

    public void updateServerMember(String email, Long serverId, Long memberId, UpdateMemberRoleRequestDTO dto)
            throws RoleException, ServerException, ServerMemberException, UserException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server  not found"));
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new UserException("Server  not found"));
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        Role role = setMemberRoleById(serverMember, dto.getRoleId());
        messagingTemplate.convertAndSend("/topic/server_member", serverMemberMapper.toDTO(serverMember));

        if (role.getName() == "OWNER") {
            User ownerUser = userService.getUserEntityByEmail(email);
            ServerMember ownerMember = serverMemberRepository.findServerMemberByUserAndServer(ownerUser, server);
            setMemberRoleByName(ownerMember, "ADMIN");
            serverService.changeOwner(server, user);

            messagingTemplate.convertAndSend("/topic/server_member", serverMemberMapper.toDTO(ownerMember));
        }
    }

    public Role setMemberRoleById(ServerMember member, Long roleId) throws RoleException {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role  not found"));
        member.setRole(role);
        serverMemberRepository.save(member);
        return role;
    }

    public Role setMemberRoleByName(ServerMember member, String roleName) throws RoleException {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleException("Role  not found"));
        member.setRole(role);
        serverMemberRepository.save(member);
        return role;
    }
}
