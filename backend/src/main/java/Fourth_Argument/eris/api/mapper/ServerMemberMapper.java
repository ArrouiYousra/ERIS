package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.model.ServerMember;

@Component
public class ServerMemberMapper {

    public ServerMemberDTO toDTO(ServerMember serverMember) {
        ServerMemberDTO dto = new ServerMemberDTO();
        dto.setServerId(serverMember.getServer().getId());
        dto.setRoleName(serverMember.getRole().getName());
        dto.setUserId(serverMember.getUser().getId());
        dto.setNickname(serverMember.getNickname());
        dto.setTypingStatus(serverMember.getTypingStatus());
        dto.setJoinedAt(serverMember.getJoinedAt());
        return dto;
    }

    // public ServerMember toEntity(ServerMemberDTO dto) {
    // ServerMember serverMember = new ServerMember();
    // serverMember.setServer(dto.getServerId());
    // serverMember.setRoleId(dto.getRole().getId());
    // serverMember.setUserId(dto.getUserId());
    // serverMember.setNickname(dto.getNickname());
    // serverMember.setTypingStatus(dto.getTypingStatus());
    // return serverMember;
    // }
}