package Fourth_Argument.eris.api.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ServerDTO {
    private Long id;
    private long ownerId;
    @NonNull
    private String name;
}
