package Fourth_Argument.eris.api.mapper;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.model.Channel;

public class ChannelMapper {

    public ChannelDTO toDTO(Channel channel) {
        return new ChannelDTO(channel.getName(), channel.getServerId());
    }

    public Channel toEntity(ChannelDTO dto) {
        Channel channel = new Channel();
        channel.setName(dto.name());
        channel.setServerId(dto.serverId());

        return channel;
    }

}
