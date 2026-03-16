package fourthargument.eris.api.services;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.ChannelDTO;
import fourthargument.eris.api.mapper.ChannelMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.UserException;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChannelDTO createChannel(Long serverId, ChannelDTO dto) throws ChannelException, ServerException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Pas de serveur existant"));

        Channel channel = channelMapper.toEntity(dto, server);
        Channel savedChannel = channelRepository.save(channel);

        if (channel.getName().isEmpty()) {
            throw new ChannelException("Le channel doit avoir un nom !");
        }

        messagingTemplate.convertAndSend("/topic/channels",
                (Object) Map.of("type", "CREATED", "channelId", savedChannel.getId()));

        return channelMapper.toDTO(savedChannel);

    }

    public List<ChannelDTO> getChannelByServer(Long serverId) throws ChannelException {

        List<Channel> channels = channelRepository.findByServerId(serverId);

        if (channels == null) {
            throw new ChannelException("Aucun channel dans ce serveur !");
        }

        List<ChannelDTO> dtoList = channels.stream()
                .map(channelMapper::toDTO)
                .toList();

        return dtoList;
    }

    public ChannelDTO findById(Long id) throws ChannelException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelException("Pas de channel existant"));

        return channelMapper.toDTO(channel);

    }

    public ChannelDTO update(ChannelDTO dto, Long id) throws ChannelException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelException("Pas de channel existant"));

        if (dto.name() != null) {
            channel.setName(dto.name());
        }
        if (dto.topic() != null) {
            channel.setTopic(dto.topic());
        }
        if (dto.isPrivate() != null) {
            channel.setIsPrivate(dto.isPrivate());
        }

        Channel updatedChannel = channelRepository.save(channel);

        return channelMapper.toDTO(updatedChannel);
    }

    public void delete(Long id) throws ChannelException, UserException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelException("Pas de channel existant"));

        List<Message> messages = messageRepository.findByChannel(channel);

        messageRepository.deleteAll(messages);

        channelRepository.delete(channel);

        messagingTemplate.convertAndSend("/topic/channels", (Object) Map.of("type", "DELETED", "serverMemberId", id));

    }
}
