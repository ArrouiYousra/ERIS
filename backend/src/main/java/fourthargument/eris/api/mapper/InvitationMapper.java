package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.InvitationDTO;
import fourthargument.eris.api.model.Invitation;

@Component
public class InvitationMapper {

    public InvitationDTO toDTO(Invitation invite) {
        InvitationDTO dto = new InvitationDTO();
        dto.setCode(invite.getCode());
        dto.setExpiresAt(invite.getExpiresAt());
        return dto;
    }

}
