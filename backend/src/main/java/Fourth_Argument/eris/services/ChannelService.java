package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.mapper.ChannelMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.ServerException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;

    public ChannelDTO createChannel(Long serverId, ChannelDTO dto) throws ChannelException, ServerException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Pas de serveur existant"));

        Channel channel = channelMapper.toEntity(dto, server);
        Channel savedChannel = channelRepository.save(channel);

        if (channel.getName().isEmpty()) {
            throw new ChannelException("Le channel doit avoir un nom !");
        }

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

    public void delete(Long id) throws ChannelException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelException("Pas de channel existant"));

        channelRepository.delete(channel);
    }

}
