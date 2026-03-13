package fourthargument.eris.api.services;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.mapper.MessageMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.MessageException;
import fourthargument.eris.exceptions.UserException;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;

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

    public void deleteMessages(List<Message> messages) {

        messageRepository.deleteAll(messages);

    }

}
