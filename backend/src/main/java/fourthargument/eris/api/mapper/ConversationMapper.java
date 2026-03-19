package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.response.ConversationDTO;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.User;

@Component
public class ConversationMapper {
    public ConversationDTO toDTO(Conversation conversation) {
        return new ConversationDTO(
            conversation.getId(),
            conversation.getSender().getId(),
            conversation.getSender().getUser(),
            conversation.getReceiver().getId(),
            conversation.getReceiver().getUser(),
            conversation.getCreatedAt()
        );
    }

    public Conversation toEntity(User sender, User receiver) {
        Conversation conversation = new Conversation();
        conversation.setSender(sender);
        conversation.setReceiver(receiver);
        return conversation;
    }
}
