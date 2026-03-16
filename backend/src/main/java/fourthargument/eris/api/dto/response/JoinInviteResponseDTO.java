package fourthargument.eris.api.dto.response;

import fourthargument.eris.api.model.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinInviteResponseDTO {
    private String serverName;
    private Long serverId;
    private Message message;
}