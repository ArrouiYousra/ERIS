package Fourth_Argument.eris.services;

import java.util.ArrayList;
import java.util.List;

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

    public List<ServerDTO> getServers() {
        List<Server> servers = serverRepository.findAll();
        // .orElseThrow(() -> new RuntimeException("No server found"));
        List<ServerDTO> serverDTOs = new ArrayList<>();
        for (Server server : servers) {
            serverDTOs.add(serverMapper.toDTO(server));
        }
        return serverDTOs;
    }

    public void createServer(ServerDTO ServerDTO) {
        Server server = serverMapper.toEntity(ServerDTO);
        serverRepository.save(server);
    }

    public void updateServer(Long id, ServerDTO ServerDTO) {
        if (serverRepository.existsById(id)) {
            ServerDTO.setId(id);
            Server server = serverMapper.toEntity(ServerDTO);
            serverRepository.save(server);
        } else
            throw new RuntimeException("Server not found");
    }

    public void deleteServer(Long id) {
        if (serverRepository.existsById(id)) {
            serverRepository.deleteById(id);
        } else
            throw new RuntimeException("Server not found");
    }
}
