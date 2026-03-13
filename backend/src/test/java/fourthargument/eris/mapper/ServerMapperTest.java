package fourthargument.eris.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fourthargument.eris.api.dto.ServerDTO;
import fourthargument.eris.api.mapper.ServerMapper;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;

class ServerMapperTest {

    private final ServerMapper mapper = new ServerMapper();

    @Test
    void toDTO() {
        User user = new User();
        user.setId(1L);
        user.setEmail("owner@test.com");

        Server server = new Server();
        server.setId(1L);
        server.setName("Test Server");
        server.setOwner(user);

        ServerDTO dto = mapper.toDTO(server, user);

        assertEquals(1L, dto.getId());
        assertEquals("Test Server", dto.getName());
        assertEquals(1L, dto.getOwnerId());
    }

    @Test
    void toEntity() {
        User user = new User();
        user.setId(1L);
        user.setEmail("owner@test.com");

        ServerDTO dto = new ServerDTO();
        dto.setId(2L);
        dto.setName("New Server");
        dto.setOwnerId(1L);

        Server entity = mapper.toEntity(dto, user);

        assertEquals(2L, entity.getId());
        assertEquals("New Server", entity.getName());
        assertEquals(user, entity.getOwner());
    }
}
