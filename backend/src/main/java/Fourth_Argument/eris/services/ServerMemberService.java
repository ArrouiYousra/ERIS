package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;

@Service
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;

    public ServerMemberService(ServerMemberRepository ServerMemberRepository, ServerMemberMapper ServerMemberMapper) {
        this.serverMemberRepository = ServerMemberRepository;
        this.serverMemberMapper = ServerMemberMapper;
    }

    public void createServerMember(Long serverId, Long userId) {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, serverId);

        if (serverMember != null) {
            throw new RuntimeException("ServerMember already exists");
        }

        serverMember = new ServerMember(userId, serverId);
        serverMemberRepository.save(serverMember);
    }

    public void deleteServerMember(Long serverId, Long userId) {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, serverId);

        if (serverMember == null) {
            throw new RuntimeException("ServerMember not found");
        }

        serverMemberRepository.deleteById(serverMember.getId());
    }

    public List<ServerMemberDTO> getServerMembers(Long id) {
        List<ServerMember> serverMembers = serverMemberRepository.findServerMemberByServerId(id);

        if (serverMembers == null) {
            throw new RuntimeException("No member found");
        }

        List<ServerMemberDTO> serverMemberDTOs = serverMembers.stream()
                .map(serverMemberMapper::toDTO)
                .toList();

        return serverMemberDTOs;
    }

    public void updateServerMember(Long id, Long userId, Long roleId) {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, id);

        if (serverMember == null) {
            throw new RuntimeException("ServerMember not found");
        }

        // tu ne peux pas changer le tien, il faut toujours un owner
        // si tu changes en owner, le tien change en admin
        serverMember.setRoleId(roleId);
        serverMemberRepository.save(serverMember);
    }
}
