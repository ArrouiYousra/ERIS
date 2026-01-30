package Fourth_Argument.eris.services;

import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.mapper.ChannelMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;

    public ChannelDTO createChannel(Long serverId, ChannelDTO dto) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Pas de serveur existant"));

        Channel channel = channelMapper.toEntity(dto, server);
        Channel savedChannel = channelRepository.save(channel);

        if (channel.getName().isEmpty()) {
            throw new RuntimeException("Le channel doit avoir un nom !");
        }

        return channelMapper.toDTO(savedChannel);

    }

}
