package fourthargument.eris.api.dto.response;

import java.util.List;

import java.time.LocalDateTime;

public record ConversationPreviewDTO(
    Long conversationId,
    List<ParticipantDTO> participants,
    LastPrivateMessageDTO lastPrivateMessage
) {
    public record ParticipantDTO(
        Long userId,
        String username
    ) {}

    public record LastPrivateMessageDTO(
        Long messageId,
        Long senderId,
        String content,
        LocalDateTime createdAt
    ) {}
}
