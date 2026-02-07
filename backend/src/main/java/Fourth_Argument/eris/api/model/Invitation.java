package Fourth_Argument.eris.api.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(name = "code")
    private String code;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public Invitation() {
    };

}
