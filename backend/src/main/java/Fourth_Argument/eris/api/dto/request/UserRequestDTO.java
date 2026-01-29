package Fourth_Argument.eris.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String displayName;

}
