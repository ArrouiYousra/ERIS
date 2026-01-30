package Fourth_Argument.eris.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "server_member")
public class ServerMember {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name = "server_id")
    private Long serverId;
    @NonNull
    @Column(name = "role_id")
    private Long roleId;
    @NonNull
    @Column(name = "user_id")
    private Long userId;
    private String nickname;
    @Column(name = "typing_status")
    private boolean typingStatus;
    @NonNull
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public ServerMember(){}
}
