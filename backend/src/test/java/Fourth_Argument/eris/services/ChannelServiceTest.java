package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fourthargument.eris.api.dto.ChannelDTO;
import fourthargument.eris.api.mapper.ChannelMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.services.ChannelService;
import fourthargument.eris.api.services.UserService;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.ServerException;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserService userService;
    @Mock
    private ServerMemberRepository serverMemberRepository;

    @InjectMocks
    private ChannelService channelService;

    private Server server;
    private Channel channel;
    private ChannelDTO channelDTO;

    @BeforeEach
    void setUp() {
        server = new Server();
        server.setId(1L);
        server.setName("Test Server");

        channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setTopic("General chat");
        channel.setIsPrivate(false);
        channel.setServer(server);

        channelDTO = new ChannelDTO(1L, "general", "General chat", false, 1L);
    }

    // ── createChannel ──

    @Test
    void createChannel_success() throws Exception {
        ChannelDTO inputDto = new ChannelDTO(null, "general", null, false, 1L);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(channelMapper.toEntity(inputDto, server)).thenReturn(channel);
        when(channelRepository.save(channel)).thenReturn(channel);
        when(channelMapper.toDTO(channel)).thenReturn(channelDTO);

        ChannelDTO result = channelService.createChannel(1L, inputDto);

        assertNotNull(result);
        assertEquals("general", result.name());
        verify(channelRepository).save(channel);
    }

    @Test
    void createChannel_serverNotFound() {
        ChannelDTO inputDto = new ChannelDTO(null, "general", null, false, 99L);
        when(serverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> channelService.createChannel(99L, inputDto));
    }

    @Test
    void createChannel_emptyName() {
        Channel emptyNameChannel = new Channel();
        emptyNameChannel.setName("");
        emptyNameChannel.setServer(server);

        ChannelDTO inputDto = new ChannelDTO(null, "", null, false, 1L);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(channelMapper.toEntity(inputDto, server)).thenReturn(emptyNameChannel);
        when(channelRepository.save(emptyNameChannel)).thenReturn(emptyNameChannel);

        assertThrows(ChannelException.class,
                () -> channelService.createChannel(1L, inputDto));
    }

    // ── getChannelByServer ──

    @Test
    void getChannelByServer_success() throws Exception {
        when(channelRepository.findByServerId(1L)).thenReturn(List.of(channel));
        when(channelMapper.toDTO(channel)).thenReturn(channelDTO);

        List<ChannelDTO> result = channelService.getChannelByServer(1L);

        assertEquals(1, result.size());
        assertEquals("general", result.get(0).name());
    }

    @Test
    void getChannelByServer_nullChannels() {
        when(channelRepository.findByServerId(1L)).thenReturn(null);

        assertThrows(ChannelException.class,
                () -> channelService.getChannelByServer(1L));
    }

    // ── findById ──

    @Test
    void findById_success() throws Exception {
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(channelMapper.toDTO(channel)).thenReturn(channelDTO);

        ChannelDTO result = channelService.findById(1L);

        assertEquals("general", result.name());
    }

    @Test
    void findById_notFound() {
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ChannelException.class,
                () -> channelService.findById(99L));
    }

    // ── update ──

    @Test
    void update_success_allFields() throws Exception {
        ChannelDTO updateDto = new ChannelDTO(null, "renamed", "new topic", true, null);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(channelRepository.save(channel)).thenReturn(channel);
        when(channelMapper.toDTO(channel)).thenReturn(new ChannelDTO(1L, "renamed", "new topic", true, 1L));

        ChannelDTO result = channelService.update(updateDto, 1L);

        assertNotNull(result);
        assertEquals("renamed", result.name());
        verify(channelRepository).save(channel);
    }

    @Test
    void update_partialFields_nameOnly() throws Exception {
        ChannelDTO updateDto = new ChannelDTO(null, "renamed", null, null, null);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(channelRepository.save(channel)).thenReturn(channel);
        when(channelMapper.toDTO(channel)).thenReturn(channelDTO);

        channelService.update(updateDto, 1L);

        assertEquals("renamed", channel.getName());
        assertEquals("General chat", channel.getTopic()); // unchanged
    }

    @Test
    void update_notFound() {
        ChannelDTO updateDto = new ChannelDTO(null, "renamed", null, null, null);
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ChannelException.class,
                () -> channelService.update(updateDto, 99L));
    }

    // ── delete ──

    @Test
    void delete_success() throws Exception {
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(messageRepository.findByChannel(channel)).thenReturn(Collections.emptyList());

        channelService.delete(1L);

        verify(messageRepository).deleteAll(Collections.emptyList());
        verify(channelRepository).delete(channel);
    }

    @Test
    void delete_notFound() {
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ChannelException.class,
                () -> channelService.delete(99L));
    }
}
