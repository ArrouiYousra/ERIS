package fourthargument.eris.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPrivateMessageRequestDTO {
    private String content;

    public Long getConversationId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getConversationId'");
    }
}
