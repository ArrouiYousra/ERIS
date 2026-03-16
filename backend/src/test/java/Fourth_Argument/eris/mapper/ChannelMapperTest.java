package Fourth_Argument.eris.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fourth_argument.eris.api.dto.ChannelDTO;
import fourth_argument.eris.api.mapper.ChannelMapper;
import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.User;

class ChannelMapperTest {

    private final ChannelMapper mapper = new ChannelMapper();

    @Test
    void toDTO_withServer() {
        User owner = new User();
        owner.setId(1L);
        Server server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(owner);

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setTopic("Chat");
        channel.setIsPrivate(true);
        channel.setServer(server);

        ChannelDTO dto = mapper.toDTO(channel);

        assertEquals(1L, dto.id());
        assertEquals("general", dto.name());
        assertEquals("Chat", dto.topic());
        assertTrue(dto.isPrivate());
        assertEquals(1L, dto.serverId());
    }

    @Test
    void toDTO_withoutServer() {
        Channel channel = new Channel();
        channel.setId(2L);
        channel.setName("orphan");
        channel.setServer(null);

        ChannelDTO dto = mapper.toDTO(channel);

        assertNull(dto.serverId());
    }

    @Test
    void toEntity_withIsPrivateNull() {
        User owner = new User();
        owner.setId(1L);
        Server server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(owner);

        ChannelDTO dto = new ChannelDTO(null, "test", "topic", null, 1L);
        Channel entity = mapper.toEntity(dto, server);

        assertEquals("test", entity.getName());
        assertEquals("topic", entity.getTopic());
        assertFalse(entity.getIsPrivate());
    }

    @Test
    void toEntity_withIsPrivateTrue() {
        User owner = new User();
        owner.setId(1L);
        Server server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(owner);

        ChannelDTO dto = new ChannelDTO(null, "private", null, true, 1L);
        Channel entity = mapper.toEntity(dto, server);

        assertTrue(entity.getIsPrivate());
    }
}
