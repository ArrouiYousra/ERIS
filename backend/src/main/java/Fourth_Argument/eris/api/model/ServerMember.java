package Fourth_Argument.eris.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String nickname;
    @NonNull
    @Column(name = "typing_status")
    private Boolean typingStatus;
    @NonNull
    @Column(name = "joined_at")
    private final LocalDateTime joinedAt;

    public ServerMember() {
        this.joinedAt = LocalDateTime.now();
    }

    public ServerMember(User user, Server server, Role role) {
        this.user = user;
        this.role = role;
        this.server = server;
        this.nickname = user.getUsername();
        this.joinedAt = LocalDateTime.now();

    }
}
