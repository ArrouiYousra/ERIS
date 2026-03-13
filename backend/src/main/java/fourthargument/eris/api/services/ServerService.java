package fourthargument.eris.api.services;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.ServerDTO;
import fourthargument.eris.api.mapper.ServerMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.InvitationRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.UserException;

import jakarta.persistence.EntityManager;

@Service
@AllArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final ServerMemberRepository serverMemberRepository;
    private final ChannelRepository channelRepository;
    private final RoleRepository roleRepository;
    private final MessageRepository messageRepository;
    private final InvitationRepository invitationRepository;
    private final EntityManager entityManager;
    private final SimpMessagingTemplate messagingTemplate;

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

    public List<ServerDTO> getServersByUser(User user) throws ServerException {
        List<Server> servers = serverRepository.findAllByUser(user);

        if (servers == null) {
            throw new ServerException("No server found");
        }

        List<ServerDTO> serverDTOs = servers.stream()
                .map(server -> serverMapper.toDTO(server, server.getOwner()))
                .toList();

        return serverDTOs;
    }

    public ServerDTO createServer(ServerDTO serverDTO, User owner) {
        Server server = new Server();
        server.setName(serverDTO.getName());
        server.setOwner(owner);
        Server savedServer = serverRepository.save(server);

        // Create default "général" channel
        Channel defaultChannel = new Channel();
        defaultChannel.setName("général");
        defaultChannel.setServer(savedServer);
        channelRepository.save(defaultChannel);

        // Create server member for owner
        ServerMember member = new ServerMember();
        member.setServer(savedServer);
        member.setUser(owner);
        member.setNickname(owner.getUser());
        member.setTypingStatus(false);

        Role ownerRole = roleRepository.findByName("OWNER")
                .orElseThrow(() -> new RuntimeException("Role OWNER not found"));
        member.setRole(ownerRole);
        serverMemberRepository.save(member);
        messagingTemplate.convertAndSend("/topic/servers",
                (Object) Map.of("type", "CREATED", "serverId", savedServer.getId()));

        return serverMapper.toDTO(savedServer, owner);
    }

    public void updateServer(Long id, ServerDTO serverDTO) throws ServerException, UserException {
        if (serverRepository.existsById(id)) {

            Server server = serverRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
            serverRepository.save(server);
        } else {
            throw new ServerException("Server not found");
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteServer(Long id) throws ServerException {
        if (!serverRepository.existsById(id)) {
            throw new ServerException("Server not found");
        }

        // Order matters: messages -> invitations -> members -> channels -> server
        messageRepository.deleteAllByServerId(id);
        invitationRepository.deleteAllByServerId(id);
        serverMemberRepository.deleteAllByServerId(id);
        channelRepository.deleteAllByServerId(id);

        // Clear persistence context to avoid stale entity references
        entityManager.flush();
        entityManager.clear();

        serverRepository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/servers", (Object) Map.of("type", "DELETED", "serverId", id));
    }

}
