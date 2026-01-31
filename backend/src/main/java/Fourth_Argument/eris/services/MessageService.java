package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.mapper.MessageMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;

    public MessageDTO sendMessage(MessageDTO dto, Long channelId) {

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        Message messageSend = messageMapper.toEntity(dto, sender, channel);

        Message saved = messageRepository.save(messageSend);

        return messageMapper.toDTO(saved);

    }

    public List<MessageDTO> getMessageHistory(Channel channel) {

        List<Message> messages = messageRepository.findByChannel(channel);

        if (messages == null) {
            throw new RuntimeException("Aucun message dans ce serveur !");
        }

        List<MessageDTO> dtoList = messages.stream()
                .map(messageMapper::toDTO)
                .toList();

        return dtoList;

    }

    public void deleteMessage(Long messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Pas de message trouvé"));

        messageRepository.delete(message);

    }

}
