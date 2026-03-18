package fourthargument.eris.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.ServerMemberDTO;
import fourthargument.eris.api.dto.request.UpdateMemberRoleRequestDTO;
import fourthargument.eris.api.mapper.ServerMemberMapper;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.RoleException;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;
import fourthargument.eris.exceptions.UserException;
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
        serverMember = serverMemberRepository.save(serverMember);
        messagingTemplate.convertAndSend("/topic/server_member/" + server.getId(), (Object) Map.of("type", "CREATED", "serverMemberId", serverMember.getId()));
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
        messagingTemplate.convertAndSend("/topic/server_member/" + serverId, (Object) Map.of("type", "DELETED", "serverMemberId", serverMember.getId()));
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
                .orElseThrow(() -> new UserException("User  not found"));
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        Role role = setMemberRoleById(serverMember, dto.getRoleId());
        messagingTemplate.convertAndSend("/topic/server_member/" + serverId, serverMemberMapper.toDTO(serverMember));

        if ("OWNER".equals(role.getName())) {
            User ownerUser = userService.getUserEntityByEmail(email);
            ServerMember ownerMember = serverMemberRepository.findServerMemberByUserAndServer(ownerUser, server);
            setMemberRoleByName(ownerMember, "ADMIN");
            serverService.changeOwner(server, user);

            messagingTemplate.convertAndSend("/topic/server_member/" + serverId, serverMemberMapper.toDTO(ownerMember));
        }
    }

    public Role setMemberRoleById(ServerMember member, Long roleId) throws RoleException {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role  not found"));
        member.setRole(role);
        member = serverMemberRepository.save(member);
        return role;
    }

    public Role setMemberRoleByName(ServerMember member, String roleName) throws RoleException {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleException("Role  not found"));
        member.setRole(role);
        member = serverMemberRepository.save(member);
        return role;

    }
}
