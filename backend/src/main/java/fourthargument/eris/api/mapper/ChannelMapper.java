package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.ChannelDTO;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Server;

@Component
public class ChannelMapper {

    public ChannelDTO toDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                channel.getName(),
                channel.getTopic(),
                channel.getIsPrivate(),
                channel.getServer() != null ? channel.getServer().getId() : null);
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
