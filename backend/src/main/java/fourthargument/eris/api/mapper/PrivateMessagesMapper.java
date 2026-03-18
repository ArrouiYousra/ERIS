package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.PrivateMessagesDTO;
import fourthargument.eris.api.model.PrivateMessage;

@Component
public class PrivateMessagesMapper {
    public PrivateMessagesDTO toDTO(PrivateMessage privateMessage) {
        return new PrivateMessagesDTO(
            privateMessage.getId(),
            privateMessage.getSender().getId(),
            privateMessage.getSender().getUsername(),
            privateMessage.getReceiver().getId(),
            privateMessage.getReceiver().getUsername(),
            privateMessage.getConversation().getId(),
            privateMessage.getContent(),
            privateMessage.getCreatedAt(),
            privateMessage.getUpdatedAt()
        );
    }

    public PrivateMessage toEntity(PrivateMessagesDTO privateMessagesDTO) {
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setId(privateMessagesDTO.id());
        privateMessage.setSender(privateMessagesDTO.senderId());
        privateMessage.setReceiver(privateMessagesDTO.receiverId());
    }

}