package fourthargument.eris.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
@Table(name = "reactions")
public class Reaction {
    @NonNull
    User user;
    @NonNull
    Message message;
    @NonNull
    String emoji;

    public Reaction(User user, Message message, String emoji) {
        this.user = user;
        this.message = message;
        this.emoji = emoji;
    }
}
