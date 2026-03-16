package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.User;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getUser(),
                message.getContent(),
                message.getChannel().getId(),
                message.getCreatedAt() != null ? message.getCreatedAt().toString() : null);
    }

    public Message toEntity(MessageDTO dto, User sender, Channel channel) {
        Message message = new Message();
        message.setChannel(channel);
        message.setContent(dto.content());
        message.setSender(sender);
        return message;
    }
}