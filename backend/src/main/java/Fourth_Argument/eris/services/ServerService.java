package Fourth_Argument.eris.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.response.ServerMemberResponseDTO;
import Fourth_Argument.eris.api.mapper.ServerMapper;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.RoleRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;

@Service
public class ServerService {
    private static final String DEFAULT_MEMBER_ROLE_NAME = "MEMBER";

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final ServerMemberRepository serverMemberRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ServerService(ServerRepository serverRepository, ServerMapper serverMapper,
                         ServerMemberRepository serverMemberRepository, RoleRepository roleRepository,
                         UserRepository userRepository) {
        this.serverRepository = serverRepository;
        this.serverMapper = serverMapper;
        this.serverMemberRepository = serverMemberRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public ServerDTO getServerById(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));
        return serverMapper.toDTO(server);
    }

    public List<ServerDTO> getServers() {
        List<Server> servers = serverRepository.findAll();
        // .orElseThrow(() -> new RuntimeException("No server found"));
        List<ServerDTO> serverDTOs = new ArrayList<>();
        for (Server server : servers) {
            serverDTOs.add(serverMapper.toDTO(server));
        }
        return serverDTOs;
    }

    public void createServer(ServerDTO ServerDTO) {
        Server server = serverMapper.toEntity(ServerDTO);
        serverRepository.save(server);
    }

    public void updateServer(Long id, ServerDTO ServerDTO) {
        if (serverRepository.existsById(id)) {
            ServerDTO.setId(id);
            Server server = serverMapper.toEntity(ServerDTO);
            serverRepository.save(server);
        } else
            throw new RuntimeException("Server not found");
    }

    public void deleteServer(Long id) {
        if (serverRepository.existsById(id)) {
            serverRepository.deleteById(id);
        } else
            throw new RuntimeException("Server not found");
    }

    public void joinServer(Long serverId, Long userId) {
        if (!serverRepository.existsById(serverId)) {
            throw new RuntimeException("Server not found");
        }
        if (serverMemberRepository.existsByServerIdAndUserId(serverId, userId)) {
            throw new RuntimeException("Already a member of this server");
        }
        Role memberRole = roleRepository.findByServerIdAndName(serverId, DEFAULT_MEMBER_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("Default role MEMBER not found for server"));
        ServerMember member = new ServerMember();
        member.setServerId(serverId);
        member.setUserId(userId);
        member.setRoleId(memberRole.getId());
        member.setJoinedAt(LocalDateTime.now());
        member.setTypingStatus(false);
        serverMemberRepository.save(member);
    }

    public void leaveServer(Long serverId, Long userId) {
        ServerMember member = serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member of this server"));
        serverMemberRepository.delete(member);
    }

    public List<ServerMemberResponseDTO> getServerMembers(Long serverId) {
        if (!serverRepository.existsById(serverId)) {
            throw new RuntimeException("Server not found");
        }
        List<ServerMember> members = serverMemberRepository.findByServerId(serverId);
        List<ServerMemberResponseDTO> result = new ArrayList<>();
        for (ServerMember m : members) {
            User user = userRepository.findById(m.getUserId()).orElse(null);
            Role role = roleRepository.findById(m.getRoleId()).orElse(null);
            ServerMemberResponseDTO dto = new ServerMemberResponseDTO();
            dto.setId(m.getId());
            dto.setUserId(m.getUserId());
            dto.setUsername(user != null ? user.getUsername() : null);
            dto.setDisplayName(user != null ? user.getDisplayName() : null);
            dto.setRoleId(m.getRoleId());
            dto.setRoleName(role != null ? role.getName() : null);
            dto.setNickname(m.getNickname());
            dto.setJoinedAt(m.getJoinedAt());
            result.add(dto);
        }
        return result;
    }

    public void updateMemberRole(Long serverId, Long userId, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        if (!role.getServerId().equals(serverId)) {
            throw new RuntimeException("Role does not belong to this server");
        }
        ServerMember member = serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setRoleId(roleId);
        serverMemberRepository.save(member);
    }
}
