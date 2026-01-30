package Fourth_Argument.eris.services;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.mapper.ChannelMapper;
import Fourth_Argument.eris.api.model.Channel;

@Service
public class ChannelService {

    private ChannelMapper channelMapper;

    public ChannelDTO createChannel(ChannelDTO dto) {

        Channel newChannel = channelMapper.toEntity(dto);

        if (newChannel.getName().isEmpty()) {
            throw new RuntimeException("Le channel doit avoir un nom !");
        }

        return channelMapper.toDTO(newChannel);

    }

}
