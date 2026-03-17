package fourthargument.eris.api.model;

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

@Entity
@Data
@Table(name = "banned_members")
public class BannedMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne
    @JoinColumn(name = "banned_by_id", nullable = false)
    private User bannedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public BannedMember() {}

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isPermanent() {
        return expiresAt == null;
    }
}