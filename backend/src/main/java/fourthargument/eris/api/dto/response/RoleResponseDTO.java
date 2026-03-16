package fourthargument.eris.api.dto.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RoleResponseDTO {
    @NonNull
    private Long id;
    @NonNull
    private String name;
}
