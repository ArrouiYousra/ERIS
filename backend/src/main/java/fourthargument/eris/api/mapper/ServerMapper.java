<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/mapper/ServerMapper.java
package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.ServerDTO;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;
========
package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.ServerDTO;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.User;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/mapper/ServerMapper.java

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