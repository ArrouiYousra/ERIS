package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.User;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getUser(),
                message.getContent(),
                message.getChannel().getId(),
                message.getCreatedAt() != null ? message.getCreatedAt() : null);
    }

    public Message toEntity(MessageDTO dto, User sender, Channel channel) {
        Message message = new Message();
        message.setChannel(channel);
        message.setContent(dto.content());
        message.setSender(sender);
        return message;
    }
}