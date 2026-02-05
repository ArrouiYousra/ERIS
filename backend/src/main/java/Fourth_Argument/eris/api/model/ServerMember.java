package Fourth_Argument.eris.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "server_member")
public class ServerMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name = "server_id")
    private Long serverId;
    @NonNull
    @Column(name = "role_id")
    private Long roleId = 1L;
    @NonNull
    @Column(name = "user_id")
    private Long userId;
    private String nickname;
    @NonNull
    @Column(name = "typing_status")
    private Boolean typingStatus;
    @NonNull
    @Column(name = "joined_at")
    private final LocalDateTime joinedAt;

    public ServerMember() {
        this.roleId = 1L;
        this.joinedAt = LocalDateTime.now();
    }

    public ServerMember(Long userId, Long serverId) {
        this.serverId = serverId;
        this.roleId = 1L;
        this.userId = userId;
        this.joinedAt = LocalDateTime.now();
    }
}
