package fourthargument.eris.api.dto.response;

import java.time.LocalDateTime;

public record PrivateMessagesDTO(Long id, Long senderId, String senderUsername, Long receiverId,
        String receiverUsername, Long conversationId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {

}
