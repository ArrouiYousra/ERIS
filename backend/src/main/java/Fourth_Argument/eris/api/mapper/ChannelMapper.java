package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;

@Component
public class ChannelMapper {

    public ChannelDTO toDTO(Channel channel) {
        return new ChannelDTO(
            channel.getId(),
            channel.getName(),
            channel.getTopic(),
            channel.getIsPrivate(),
            channel.getServer() != null ? channel.getServer().getId() : null
        );
    }

    public Channel toEntity(ChannelDTO dto, Server server) {
        Channel channel = new Channel();
        channel.setName(dto.name());
        channel.setTopic(dto.topic());
        channel.setIsPrivate(dto.isPrivate() != null ? dto.isPrivate() : false);
        channel.setServer(server);

        return channel;
    }

}
