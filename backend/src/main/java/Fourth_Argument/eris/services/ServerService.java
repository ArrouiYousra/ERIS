package Fourth_Argument.eris.services;

import java.util.ArrayList;
import java.util.List;

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
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;
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

    public ServerDTO getServerById(Long id) throws ServerException {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ServerException("Server not found"));
        return serverMapper.toDTO(server, null);
    }

    public List<ServerDTO> getServers() throws ServerException {
        List<Server> servers = serverRepository.findAll();

        if (servers == null) {
            throw new ServerException("No server found");
        }

        List<ServerDTO> serverDTOs = servers.stream()
                .map(server -> serverMapper.toDTO(server, server.getOwner()))
                .toList();

        return serverDTOs;
    }

    public List<ServerDTO> getUserServers(Long id) throws ServerException {
        List<ServerMember> serverMembers = serverMemberRepository.findServerMemberByUserId(id);

        if (serverMembers == null) {
            throw new ServerException("No server found");
        }

        List<ServerDTO> serverDTOs = new ArrayList<>();
        for (ServerMember serverMember : serverMembers) {
            serverDTOs.add(getServerById(serverMember.getServerId()));
        }

        return serverDTOs;
    }

    public void createServer(ServerDTO serverDTO, User owner) {
        Server server = new Server();
        server.setName(serverDTO.getName());
        server.setOwner(owner);
        serverRepository.save(server);
    }

    public void updateServer(Long id, ServerDTO serverDTO) throws ServerException, UserException {
        if (serverRepository.existsById(id)) {

            Server server = serverRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
            serverRepository.save(server);
        } else {
            throw new ServerException("Server not found");
        }
    }

    public void deleteServer(Long id) throws ChannelException, ServerException, ServerMemberException {
        if (serverRepository.existsById(id)) {

            List<ServerMember> serverMembers = serverMemberRepository.findServerMemberByServerId(id);
            for (ServerMember serverMember : serverMembers) {
                serverMemberService.deleteServerMember(id, serverMember.getUserId());
            }

            List<Channel> channels = channelRepository.findByServerId(id);
            for (Channel channel : channels) {
                channelService.delete(channel.getId());
            }

            serverRepository.deleteById(id);
        } else {
            throw new ServerException("Server not found");
        }
    }
}
