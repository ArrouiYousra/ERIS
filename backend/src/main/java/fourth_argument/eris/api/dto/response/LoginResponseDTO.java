package fourth_argument.eris.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }

}
