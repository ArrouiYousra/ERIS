<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/model/Channel.java
package fourthargument.eris.api.model;
========
package fourth_argument.eris.api.model;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/model/Channel.java

import java.sql.Date;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "channel")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String topic;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(name = "created_at")
    private Date createdAt;

    public Channel() {
    }

    public Channel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
