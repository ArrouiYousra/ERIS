package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.ConversationDTO;
import fourthargument.eris.api.model.Conversation;

public class ConversationMapper {
    public ConversationDTO toDTO(Conversation conversation) {
        return new ConversationDTO(
            conversation.getId(),
            conversation.getSender().getId(),
            conversation.getSender().getUsername(),
            conversation.getReceiver().getId(),
            conversation.getReceiver().getUsername(),
            conversation.getCreatedAt()
        );
    }

    public Conversation toEntity(ConversationDTO conversationDTO) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationDTO.id());
        conversation.setSender(conversationDTO.senderId());
        conversation.setReceiver(conversationDTO.receiverId());
        return conversation;
    }
}
