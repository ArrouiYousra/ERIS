package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.response.PrivateMessagesDTO;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;

@Component
public class PrivateMessagesMapper {

    public PrivateMessage toEntity(String content, User sender, Conversation conversation) {
        PrivateMessage pm = new PrivateMessage();
        pm.setConversation(conversation);
        pm.setSender(sender);
        pm.setContent(content);
        return pm;
    }

    public PrivateMessagesDTO toDTO(PrivateMessage pm) {
        return new PrivateMessagesDTO(
            pm.getId(),
            pm.getConversation().getId(),
            new PrivateMessagesDTO.SenderDTO(pm.getSender().getId(), pm.getSender().getUser()),
            pm.getContent(),
            pm.getCreatedAt(),
            pm.getUpdatedAt()
        );
    }
}