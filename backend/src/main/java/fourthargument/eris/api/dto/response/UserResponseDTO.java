package fourthargument.eris.api.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserResponseDTO {

    public UserResponseDTO(long l, String string, String string2, String string3) {
    }

    private Long id;
    private String email;
    private String username;
    private String displayName;

}
