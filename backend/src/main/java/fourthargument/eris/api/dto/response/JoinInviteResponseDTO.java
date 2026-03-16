package fourthargument.eris.api.dto.response;

import Fourth_Argument.eris.api.model.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinInviteResponseDTO {
    private String serverName;
    private Long serverId;
    private Message message;
}