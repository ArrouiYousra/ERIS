package Fourth_Argument.eris.messagingstompwebsocket.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
public class User {
    @Id
    private String displayName;
    private UserStatus status;
    private TypingStatus typing;
}
