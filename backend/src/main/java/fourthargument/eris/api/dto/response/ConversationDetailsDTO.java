package fourthargument.eris.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationDetailsDTO(
    Long conversationId,
    List<ParticipantDTO> participants,
    List<PrivateMessageDTO> privateMessages
) {

    public record ParticipantDTO(
        Long userId,
        String username
    ) {}

    public record PrivateMessageDTO(
        Long messageId,
        Long senderId,
        String senderUsername,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}   
