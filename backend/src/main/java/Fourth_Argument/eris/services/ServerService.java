package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.mapper.ServerMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberService serverMemberService;
    private final ChannelRepository channelRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public ServerDTO getServerById(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));
        return serverMapper.toDTO(server, null);
    }

    public List<ServerDTO> getServers() {
        List<Server> servers = serverRepository.findAll();

        if (servers == null) {
            throw new RuntimeException("No server found");
        }

        List<ServerDTO> serverDTOs = servers.stream()
                .map(server -> serverMapper.toDTO(server, server.getOwner()))
                .toList();

        return serverDTOs;
    }

    public List<ServerDTO> getServersByUser(User user) {
        List<Server> servers = serverRepository.findByOwner(user);

        if (servers == null) {
            throw new RuntimeException("No server found");
        }

        List<ServerDTO> serverDTOs = servers.stream()
                .map(server -> serverMapper.toDTO(server, server.getOwner()))
                .toList();

        return serverDTOs;
    }

    public List<ServerDTO> getUserServers(Long id) {
        List<ServerMember> serverMembers = serverMemberRepository.findServerMemberByUserId(id);

        if (serverMembers == null) {
            throw new RuntimeException("No server found");
        }

        List<ServerDTO> serverDTOs = serverMembers.stream()
                .map(member -> getServerById(member.getServerId()))
                .toList();

        return serverDTOs;
    }

    public void createServer(ServerDTO serverDTO, User owner) {
        Server server = new Server();
        server.setName(serverDTO.getName());
        server.setOwner(owner);
        serverRepository.save(server);
    }

    public void updateServer(Long id, ServerDTO serverDTO) {
        if (serverRepository.existsById(id)) {

            Server server = serverRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            serverRepository.save(server);
        } else {
            throw new RuntimeException("Server not found");
        }
    }

    public void deleteServer(Long id) {
        if (serverRepository.existsById(id)) {

            serverMemberRepository.findServerMemberByServerId(id).stream()
                    .forEach(member -> serverMemberService.deleteServerMember(id, member.getUserId()));

            channelRepository.findByServerId(id).stream()
                    .forEach(channel -> channelService.delete(channel.getId()));

            serverRepository.deleteById(id);
        } else {
            throw new RuntimeException("Server not found");
        }
    }
}
