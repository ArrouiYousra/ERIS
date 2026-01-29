package Fourth_Argument.eris.api.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "server")
public class Server {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long owner_id;
    private String name;

    public Server(){}

    public Server(Long id, Long owner_id) {
        this(id, owner_id, "New Server");
    }

    public Server(Long id, Long owner_id, String name) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
    }
}
