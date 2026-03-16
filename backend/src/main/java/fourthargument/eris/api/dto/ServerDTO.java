<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/dto/ServerDTO.java
package fourthargument.eris.api.dto;
========
package fourth_argument.eris.api.dto;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/dto/ServerDTO.java

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerDTO {
    private Long id;
    private Long ownerId;
    private String name;
}
