package Fourth_Argument.eris.api.dto;

import java.time.LocalDateTime;

public record MessageDTO(Long id, Long senderId, String senderUsername, String content, Long channelId,
        LocalDateTime createdAt) {
}
