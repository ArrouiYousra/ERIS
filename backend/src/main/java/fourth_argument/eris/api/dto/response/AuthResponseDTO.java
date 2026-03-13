package fourth_argument.eris.api.dto.response;

import com.mongodb.lang.NonNull;

import fourth_argument.eris.api.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthResponseDTO {

    @NonNull
    private String token;

    @NonNull
    private User user;

}
