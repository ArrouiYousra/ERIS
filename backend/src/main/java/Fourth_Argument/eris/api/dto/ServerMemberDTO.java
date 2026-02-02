package Fourth_Argument.eris.api.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerMemberDTO {
    private Long serverId;
    private Long roleId;
    private Long userId;
    private String nickname;
    private Boolean typingStatus;
    private LocalDateTime joinedAt;
}
