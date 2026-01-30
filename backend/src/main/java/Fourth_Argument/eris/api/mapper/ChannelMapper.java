package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;

@Component
public class ChannelMapper {

    public ChannelDTO toDTO(Channel channel) {
        return new ChannelDTO(channel.getName());
    }

    public Channel toEntity(ChannelDTO dto, Server server) {
        Channel channel = new Channel();
        channel.setName(dto.name());
        channel.setServer(server);

        return channel;
    }

}
