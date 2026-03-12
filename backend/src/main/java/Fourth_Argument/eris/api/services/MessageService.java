package Fourth_Argument.eris.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.mapper.MessageMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.MessageException;
import Fourth_Argument.eris.exceptions.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageDTO sendMessage(MessageDTO dto, Long channelId) throws ChannelException, UserException {

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new UserException("User not found"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException("Channel not found"));

        Message messageSend = messageMapper.toEntity(dto, sender, channel);

        Message saved = messageRepository.save(messageSend);

        return messageMapper.toDTO(saved);

    }

    public List<MessageDTO> getMessageHistory(Long channelId) throws MessageException, ChannelException {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException("This channel is not found !"));

        List<Message> messages = messageRepository.findByChannel(channel);

        if (messages == null) {
            throw new MessageException("Aucun message dans ce serveur !");
        }

        List<MessageDTO> dtoList = messages.stream()
                .map(messageMapper::toDTO)
                .toList();

        return dtoList;

    }

    public void deleteMessage(Long messageId) throws MessageException {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("Pas de message trouvé"));

        messageRepository.delete(message);

    }

    public Message editMessage(String email, MessageDTO messageDTO, Long messageId) throws MessageException {

        Message updatedMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("Pas de message trouvé"));

        if (!updatedMessage.getSender().getEmail().equals(email)) {
            throw new MessageException("Vous n'êtes pas autorisé à modifier ce message");
        }

        updatedMessage.setContent(messageDTO.content());
        updatedMessage.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(updatedMessage);

        messagingTemplate.convertAndSend("/topic/channels/" + updatedMessage.getChannel().getId(),
                messageMapper.toDTO(updatedMessage));

        return updatedMessage;

    }

    public void deleteMessages(List<Message> messages) {

        messageRepository.deleteAll(messages);

    }

}
