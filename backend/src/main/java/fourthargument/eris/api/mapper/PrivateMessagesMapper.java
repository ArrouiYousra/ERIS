package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.response.PrivateMessagesDTO;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;

@Component
public class PrivateMessagesMapper {
    public PrivateMessagesDTO toDTO(PrivateMessage privateMessage) {
        return new PrivateMessagesDTO(
            privateMessage.getId(),
            privateMessage.getSender().getId(),
            privateMessage.getSender().getUser(),
            privateMessage.getConversation().getReceiver().getId(),
            privateMessage.getConversation().getReceiver().getUser(),
            privateMessage.getConversation().getId(),
            privateMessage.getContent(),
            privateMessage.getCreatedAt(),
            privateMessage.getUpdatedAt()
        );
    }

    public PrivateMessage toEntity(
            PrivateMessagesDTO privateMessagesDTO,
            User sender,
            Conversation conversation) {
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setId(privateMessagesDTO.id());
        privateMessage.setSender(sender);
        privateMessage.setConversation(conversation);
        privateMessage.setContent(privateMessagesDTO.content());
        return privateMessage;
    }

}