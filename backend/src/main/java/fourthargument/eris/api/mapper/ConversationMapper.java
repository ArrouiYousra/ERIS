package fourthargument.eris.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.response.ConversationDetailsDTO;
import fourthargument.eris.api.dto.response.ConversationPreviewDTO;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;

@Component
public class ConversationMapper {

    public Conversation toEntity(User sender, User receiver) {
        Conversation c = new Conversation();
        c.getParticipants().add(sender);
        c.getParticipants().add(receiver);
        return c;
    }

    public ConversationPreviewDTO toPreviewDTO(Conversation c, PrivateMessage lastMessage) {
        return new ConversationPreviewDTO(
            c.getId(),
            c.getParticipants().stream()
            .map(u -> new ConversationPreviewDTO.ParticipantDTO(u.getId(), u.getUser()))
            .toList(),
            lastMessage == null
            ? null
            : new ConversationPreviewDTO.LastPrivateMessageDTO(
                lastMessage.getId(),
                lastMessage.getSender().getId(),
                lastMessage.getContent(),
                lastMessage.getCreatedAt()
            )
        );
    }
    
    public ConversationDetailsDTO toDetailsDTO(Conversation c, List<PrivateMessage> messages) {
        return new ConversationDetailsDTO(
            c.getId(),
            c.getParticipants().stream()
            .map(u -> new ConversationDetailsDTO.ParticipantDTO(u.getId(), u.getUser()))
            .toList(),
            messages.stream()
            .map(pm -> new ConversationDetailsDTO.PrivateMessageDTO(
                pm.getId(),
                pm.getSender().getId(),
                pm.getSender().getUser(),
                pm.getContent(),
                pm.getCreatedAt(),
                pm.getUpdatedAt()))
            .toList()
        );
    }
}
