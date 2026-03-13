package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fourthargument.eris.api.dto.ServerDTO;
import fourthargument.eris.api.mapper.ServerMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.services.ServerService;
import fourthargument.eris.exceptions.ServerException;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {

    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerMapper serverMapper;
    @Mock
    private ServerMemberRepository serverMemberRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ServerService serverService;

    private Server server;
    private User owner;
    private ServerDTO serverDTO;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setUsername("owner");

        server = new Server();
        server.setId(1L);
        server.setName("Test Server");
        server.setOwner(owner);

        serverDTO = new ServerDTO();
        serverDTO.setId(1L);
        serverDTO.setName("Test Server");
        serverDTO.setOwnerId(1L);

        ownerRole = new Role();
        ownerRole.setId(1L);
        ownerRole.setName("OWNER");
    }

    // ── getServerById ──

    @Test
    void getServerById_success() throws Exception {
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(serverMapper.toDTO(server, null)).thenReturn(serverDTO);

        ServerDTO result = serverService.getServerById(1L);

        assertNotNull(result);
        assertEquals("Test Server", result.getName());
    }

    @Test
    void getServerById_notFound() {
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> serverService.getServerById(99L));
    }

    // ── getServers ──

    @Test
    void getServers_success() throws Exception {
        when(serverRepository.findAll()).thenReturn(List.of(server));
        when(serverMapper.toDTO(server, owner)).thenReturn(serverDTO);

        List<ServerDTO> result = serverService.getServers();

        assertEquals(1, result.size());
    }

    @Test
    void getServers_null() {
        when(serverRepository.findAll()).thenReturn(null);

        assertThrows(ServerException.class,
                () -> serverService.getServers());
    }

    // ── getServersByUser ──

    @Test
    void getServersByUser_success() throws Exception {
        when(serverRepository.findAllByUser(owner)).thenReturn(List.of(server));
        when(serverMapper.toDTO(server, owner)).thenReturn(serverDTO);

        List<ServerDTO> result = serverService.getServersByUser(owner);

        assertEquals(1, result.size());
    }

    @Test
    void getServersByUser_null() {
        when(serverRepository.findAllByUser(owner)).thenReturn(null);

        assertThrows(ServerException.class,
                () -> serverService.getServersByUser(owner));
    }

    // ── createServer ──

    @Test
    void createServer_success() {
        ServerDTO inputDTO = new ServerDTO();
        inputDTO.setName("New Server");

        when(serverRepository.save(any(Server.class))).thenReturn(server);
        when(channelRepository.save(any(Channel.class))).thenReturn(new Channel());
        when(roleRepository.findByName("OWNER")).thenReturn(Optional.of(ownerRole));
        when(serverMemberRepository.save(any())).thenReturn(null);
        when(serverMapper.toDTO(server, owner)).thenReturn(serverDTO);

        ServerDTO result = serverService.createServer(inputDTO, owner);

        assertNotNull(result);
        verify(channelRepository).save(any(Channel.class));
        verify(serverMemberRepository).save(any());
    }

    @Test
    void createServer_ownerRoleNotFound() {
        ServerDTO inputDTO = new ServerDTO();
        inputDTO.setName("New Server");

        when(serverRepository.save(any(Server.class))).thenReturn(server);
        when(channelRepository.save(any(Channel.class))).thenReturn(new Channel());
        when(roleRepository.findByName("OWNER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> serverService.createServer(inputDTO, owner));
    }

    // ── updateServer ──

    @Test
    void updateServer_success() throws Exception {
        when(serverRepository.existsById(1L)).thenReturn(true);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));

        serverService.updateServer(1L, serverDTO);

        verify(serverRepository).save(server);
    }

    @Test
    void updateServer_notFound() {
        when(serverRepository.existsById(99L)).thenReturn(false);

        assertThrows(ServerException.class,
                () -> serverService.updateServer(99L, serverDTO));
    }
}
