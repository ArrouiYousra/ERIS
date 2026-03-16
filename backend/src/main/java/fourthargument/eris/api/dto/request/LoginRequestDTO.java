<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/dto/request/LoginRequestDTO.java
package fourthargument.eris.api.dto.request;
========
package fourth_argument.eris.api.dto.request;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/dto/request/LoginRequestDTO.java

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    private String email;
    private String password;
}