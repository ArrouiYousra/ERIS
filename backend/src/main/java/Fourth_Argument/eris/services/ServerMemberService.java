package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.mapper.ServerMemberMapper;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.exceptions.ServerMemberException;

@Service
public class ServerMemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberMapper serverMemberMapper;

    public ServerMemberService(ServerMemberRepository ServerMemberRepository, ServerMemberMapper ServerMemberMapper) {
        this.serverMemberRepository = ServerMemberRepository;
        this.serverMemberMapper = ServerMemberMapper;
    }

    public void createServerMember(Long serverId, Long userId) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, serverId);

        if (serverMember != null) {
            throw new ServerMemberException("ServerMember already exists");
        }

        serverMember = new ServerMember(userId, serverId);
        serverMemberRepository.save(serverMember);
    }

    public void deleteServerMember(Long serverId, Long userId) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, serverId);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        serverMemberRepository.deleteById(serverMember.getId());
    }

    public List<ServerMemberDTO> getServerMembers(Long id) throws ServerMemberException {
        List<ServerMember> serverMembers = serverMemberRepository.findServerMemberByServerId(id);

        if (serverMembers == null) {
            throw new ServerMemberException("No member found");
        }

        List<ServerMemberDTO> serverMemberDTOs = serverMembers.stream()
                .map(serverMemberMapper::toDTO)
                .toList();

        return serverMemberDTOs;
    }

    public void updateServerMember(Long id, Long userId, Long roleId) throws ServerMemberException {
        ServerMember serverMember = serverMemberRepository.findServerMemberByUserIdAndServerId(userId, id);

        if (serverMember == null) {
            throw new ServerMemberException("ServerMember not found");
        }

        // tu ne peux pas changer le tien, il faut toujours un owner
        // si tu changes en owner, le tien change en admin
        serverMember.setRoleId(roleId);
        serverMemberRepository.save(serverMember);
    }
}
