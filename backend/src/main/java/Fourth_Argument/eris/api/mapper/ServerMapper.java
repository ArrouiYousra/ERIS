package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.model.Server;

@Component
public class ServerMapper {

    public ServerDTO toDTO(Server server) {
        ServerDTO dto = new ServerDTO();
        dto.setOwnerId(server.getOwnerId());
        dto.setName(server.getName());
        return dto;
    }

    public Server toEntity(ServerDTO dto) {
        Server server = new Server();
        server.setOwnerId(dto.getOwnerId());
        server.setName(dto.getName());
        return server;
    }
}