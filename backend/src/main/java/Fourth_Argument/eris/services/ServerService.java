package Fourth_Argument.eris.services;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.mapper.ServerMapper;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.repository.ServerRepository;

@Service
public class ServerService {
    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;

    public ServerService(ServerRepository ServerRepository, ServerMapper ServerMapper) {
        this.serverRepository = ServerRepository;
        this.serverMapper = ServerMapper;
    }

    public ServerDTO getServerById(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));
        return serverMapper.toDTO(server);
    }

    public void saveServer(ServerDTO ServerDTO) {
        Server server = serverMapper.toEntity(ServerDTO);
        serverRepository.save(server);
    }
}
