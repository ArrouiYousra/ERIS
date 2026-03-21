package fourthargument.eris.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reactions")
public class Reaction {

    @EmbeddedId
    private ReactionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(nullable = false, length = 10)
    private String emoji;

    public Reaction(User user, Message message, String emoji) {
        this.id = new ReactionId(user.getId(), message.getId());
        this.user = user;
        this.message = message;
        this.emoji = emoji;
    }
}