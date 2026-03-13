package Fourth_Argument.eris.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import fourth_argument.eris.api.dto.MessageDTO;
import fourth_argument.eris.api.mapper.MessageMapper;
import fourth_argument.eris.api.model.Channel;
import fourth_argument.eris.api.model.Message;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.User;

class MessageMapperTest {

    private final MessageMapper mapper = new MessageMapper();

    @Test
    void toDTO_withCreatedAt() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@test.com");
        sender.setUsername("senderUser");

        User owner = new User();
        owner.setId(2L);
        Server server = new Server();
        server.setId(1L);
        server.setName("S");
        server.setOwner(owner);

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setServer(server);

        Message msg = new Message();
        msg.setId(1L);
        msg.setSender(sender);
        msg.setContent("Hello");
        msg.setChannel(channel);
        msg.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));

        MessageDTO dto = mapper.toDTO(msg);

        assertEquals(1L, dto.id());
        assertEquals(1L, dto.senderId());
        assertEquals("senderUser", dto.senderUsername());
        assertEquals("Hello", dto.content());
        assertEquals(1L, dto.channelId());
        assertNotNull(dto.createdAt());
    }

    @Test
    void toDTO_withNullCreatedAt() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@test.com");

        User owner = new User();
        owner.setId(2L);
        Server server = new Server();
        server.setId(1L);
        server.setName("S");
        server.setOwner(owner);

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setServer(server);

        Message msg = new Message();
        msg.setId(1L);
        msg.setSender(sender);
        msg.setContent("Hello");
        msg.setChannel(channel);
        msg.setCreatedAt(null);

        MessageDTO dto = mapper.toDTO(msg);

        assertNull(dto.createdAt());
    }

    @Test
    void toEntity() {
        User sender = new User();
        sender.setId(1L);

        User owner = new User();
        owner.setId(2L);
        Server server = new Server();
        server.setId(1L);
        server.setName("S");
        server.setOwner(owner);

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setServer(server);

        MessageDTO dto = new MessageDTO(null, 1L, null, "World", 1L, null);
        Message entity = mapper.toEntity(dto, sender, channel);

        assertEquals("World", entity.getContent());
        assertEquals(sender, entity.getSender());
        assertEquals(channel, entity.getChannel());
    }
}
