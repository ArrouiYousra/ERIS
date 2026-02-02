package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.model.ServerMember;

@Component
public class ServerMemberMapper {

    public ServerMemberDTO toDTO(ServerMember serverMember) {
        ServerMemberDTO dto = new ServerMemberDTO();
        dto.setServerId(serverMember.getServerId());
        dto.setRoleId(serverMember.getRoleId());
        dto.setUserId(serverMember.getUserId());
        dto.setNickname(serverMember.getNickname());
        dto.setTypingStatus(serverMember.getTypingStatus());
        dto.setJoinedAt(serverMember.getJoinedAt());
        return dto;
    }

    public ServerMember toEntity(ServerMemberDTO dto) {
        ServerMember serverMember = new ServerMember();
        serverMember.setServerId(dto.getServerId());
        serverMember.setRoleId(dto.getRoleId());
        serverMember.setUserId(dto.getUserId());
        serverMember.setNickname(dto.getNickname());
        serverMember.setTypingStatus(dto.getTypingStatus());
        return serverMember;
    }
}