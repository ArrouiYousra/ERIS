package Fourth_Argument.eris.api.mapper;

import Fourth_Argument.eris.api.dto.InvitationDTO;
import Fourth_Argument.eris.api.model.Invitation;

import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

    public InvitationDTO toDTO(Invitation invite) {
        InvitationDTO dto = new InvitationDTO();
        dto.setCode(invite.getCode());
        dto.setExpiresAt(invite.getExpiresAt());
        return dto;
    }

}
