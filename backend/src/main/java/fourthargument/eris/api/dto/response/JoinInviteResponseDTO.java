package fourthargument.eris.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinInviteResponseDTO {
    private String serverName;
    private Long serverId;
    private String message; // optional
}