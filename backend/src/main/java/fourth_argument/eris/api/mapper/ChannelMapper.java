package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.ChannelDTO;
import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Server;

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
