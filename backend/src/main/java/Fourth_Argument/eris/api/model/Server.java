package Fourth_Argument.eris.api.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name = "owner_id")
    private Long ownerId;
    @NonNull
    private String name;

    public Server(){}
}
