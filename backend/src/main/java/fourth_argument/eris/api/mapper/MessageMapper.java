package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.MessageDTO;
import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Message;
import fourth_argument.eris.api.model.User;

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