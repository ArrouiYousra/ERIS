<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/dto/request/UserRequestDTO.java
package fourthargument.eris.api.dto.request;
========
package fourth_argument.eris.api.dto.request;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/dto/request/UserRequestDTO.java

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    private String email;
    private String username;
    private String password;
    private String displayName;

}
