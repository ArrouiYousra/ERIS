package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.InvitationDTO;
import fourth_argument.eris.api.model.Invitation;

@Component
public class InvitationMapper {

    public InvitationDTO toDTO(Invitation invite) {
        InvitationDTO dto = new InvitationDTO();
        dto.setCode(invite.getCode());
        dto.setExpiresAt(invite.getExpiresAt());
        return dto;
    }

}
