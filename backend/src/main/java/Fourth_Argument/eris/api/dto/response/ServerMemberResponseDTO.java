package Fourth_Argument.eris.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerMemberResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String displayName;
    private Long roleId;
    private String roleName;
    private String nickname;
    private LocalDateTime joinedAt;
}
