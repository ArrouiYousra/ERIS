package Fourth_Argument.eris.api.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @NonNull
    private String name;

    public Server() {
    }
}
