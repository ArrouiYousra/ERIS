package fourth_argument.eris.api.dto;

import java.time.LocalDateTime;

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
