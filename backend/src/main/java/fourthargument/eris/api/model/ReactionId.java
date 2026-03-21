package fourthargument.eris.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "message_id")
    private Long messageId;
}