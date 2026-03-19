package fourthargument.eris.api.dto.response;

import java.time.LocalDateTime;

public record PrivateMessagesDTO(
        Long messageId,
        Long conversationId,
        SenderDTO sender,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    public record SenderDTO(
        Long userId,
        String username
    ) {}
}
