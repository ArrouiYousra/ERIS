package Fourth_Argument.eris.api.dto.response;

import com.mongodb.lang.NonNull;

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
    private UserResponseDTO userResponseDTO;

}
