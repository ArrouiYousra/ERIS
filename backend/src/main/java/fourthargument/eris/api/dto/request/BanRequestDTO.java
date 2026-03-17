package fourthargument.eris.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BanRequestDTO {
    private Long userId;
    private String reason;
    private Integer durationInHours;
}
