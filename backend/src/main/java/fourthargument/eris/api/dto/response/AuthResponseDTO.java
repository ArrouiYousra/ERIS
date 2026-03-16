package fourthargument.eris.api.dto.response;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import fourthargument.eris.api.model.User;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthResponseDTO {

    @NonNull
    private String token;

    @NonNull
    private User user;

}
