package Fourth_Argument.eris.api.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationDTO {

    private String code;
    private LocalDateTime expiresAt;

}
