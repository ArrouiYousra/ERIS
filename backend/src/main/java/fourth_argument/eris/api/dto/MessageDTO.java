package fourth_argument.eris.api.dto;

public record MessageDTO(Long id, Long senderId, String senderUsername, String content, Long channelId,
                String createdAt) {
}
