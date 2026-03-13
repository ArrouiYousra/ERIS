package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.ServerDTO;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.User;

@Component
public class ServerMapper {

    public ServerDTO toDTO(Server server, User user) {
        ServerDTO dto = new ServerDTO();
        dto.setId(server.getId());
        dto.setOwnerId(user.getId());
        dto.setName(server.getName());
        return dto;
    }

    public Server toEntity(ServerDTO dto, User user) {
        Server server = new Server();
        server.setId(dto.getId());
        server.setOwner(user);
        server.setName(dto.getName());
        return server;
    }
}