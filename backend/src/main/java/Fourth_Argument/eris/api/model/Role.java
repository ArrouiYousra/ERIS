package Fourth_Argument.eris.api.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "server_id")
    private Long serverId;
    private String name;
}
