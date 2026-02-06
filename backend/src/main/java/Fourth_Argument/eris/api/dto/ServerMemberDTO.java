package Fourth_Argument.eris.api.dto;

import java.time.LocalDateTime;

import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerMemberDTO {
    private Long userId;
    private String username;
    private Long serverId;
    private String roleName;
    private String nickname;
    private Boolean typingStatus;
    private LocalDateTime joinedAt;
}
