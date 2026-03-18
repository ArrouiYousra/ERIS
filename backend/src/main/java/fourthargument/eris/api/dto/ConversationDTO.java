package fourthargument.eris.api.dto;

import java.time.LocalDateTime;

public record ConversationDTO(Long id, Long senderId, String senderUsername, Long receiverId, 
                String receiverUsername, LocalDateTime createdAt) {   
}
