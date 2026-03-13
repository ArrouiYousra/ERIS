package fourth_argument.eris.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String email;
    private String username;
    private String displayName;

}
