package Fourth_Argument.eris.api.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private @NonNull String username;
    private @NonNull String email;
    private @NonNull String password;
    private @NonNull String displayName;

}
