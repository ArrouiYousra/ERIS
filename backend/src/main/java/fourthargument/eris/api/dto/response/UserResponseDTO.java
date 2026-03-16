<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/dto/response/UserResponseDTO.java
package fourthargument.eris.api.dto.response;
========
package fourth_argument.eris.api.dto.response;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/dto/response/UserResponseDTO.java

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private String username;
    private String displayName;

}
