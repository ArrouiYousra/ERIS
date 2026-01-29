package Fourth_Argument.eris.services;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.repository.ServerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
    private final ServerRepository serverRepository;

    @Autowired
    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    // public ServerRequestDto getServerDtoById(Long id) {
    //     Server serverEntity = serverRepository.findFirstById(id);
    //     // return serverEntity.builder()
    // }
}
