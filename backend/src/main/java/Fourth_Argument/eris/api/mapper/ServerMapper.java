package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.User;

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