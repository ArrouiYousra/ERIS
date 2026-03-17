package fourthargument.eris.services;

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

import fourthargument.eris.api.dto.MessageDTO;
import fourthargument.eris.api.mapper.MessageMapper;
import fourthargument.eris.api.model.Channel;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ChannelRepository;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.api.services.MessageService;
import fourthargument.eris.exceptions.ChannelException;
import fourthargument.eris.exceptions.MessageException;
import fourthargument.eris.exceptions.UserException;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private Channel channel;
    private Message message;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");

        Server server = new Server();
        server.setId(1L);
        server.setName("Server");
        server.setOwner(sender);

        channel = new Channel();
        channel.setId(1L);
        channel.setName("general");
        channel.setServer(server);

        message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setContent("Hello");
        message.setChannel(channel);

        messageDTO = new MessageDTO(1L, 1L, "sender@example.com", "Hello", 1L, null);
    }

    // ── sendMessage ──

    @Test
    void sendMessage_success() throws Exception {
        MessageDTO inputDTO = new MessageDTO(null, 1L, null, "Hello", 1L, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(messageMapper.toEntity(inputDTO, sender, channel)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        MessageDTO result = messageService.sendMessage(inputDTO, 1L);

        assertNotNull(result);
        assertEquals("Hello", result.content());
    }

    @Test
    void sendMessage_userNotFound() {
        MessageDTO inputDTO = new MessageDTO(null, 99L, null, "Hello", 1L, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserException.class,
                () -> messageService.sendMessage(inputDTO, 1L));
    }

    @Test
    void sendMessage_channelNotFound() {
        MessageDTO inputDTO = new MessageDTO(null, 1L, null, "Hello", 99L, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ChannelException.class,
                () -> messageService.sendMessage(inputDTO, 99L));
    }

    // ── getMessageHistory ──

    @Test
    void getMessageHistory_success() throws Exception {
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));
        when(messageRepository.findByChannel(channel)).thenReturn(List.of(message));
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        List<MessageDTO> result = messageService.getMessageHistoryChannel(channel.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getMessageHistory_null() {
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));

        // 2. ENSUITE, on simule le retour null des messages
        when(messageRepository.findByChannel(channel)).thenReturn(null);

        // 3. ASSERTION : On s'attend à une MessageException (et non ChannelException !)
        // car c'est le messageRepository qui renvoie null ici.
        assertThrows(MessageException.class,
                () -> messageService.getMessageHistoryChannel(channel.getId()));
    }

    @Test
    void getMessageHistory_empty() throws Exception {
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));
        when(messageRepository.findByChannel(channel)).thenReturn(Collections.emptyList());

        List<MessageDTO> result = messageService.getMessageHistoryChannel(channel.getId());

        assertTrue(result.isEmpty());
    }

    // ── deleteMessage ──

    @Test
    void deleteMessage_success() throws Exception {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        messageService.deleteMessage(1L);

        verify(messageRepository).delete(message);

    }

    @Test
    void deleteMessage_notFound() {
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MessageException.class,
                () -> messageService.deleteMessage(99L));
    }

    // ── deleteMessages ──

    @Test
    void deleteMessages_success() {
        List<Message> messages = List.of(message);

        messageService.deleteMessages(messages);

        verify(messageRepository).deleteAll(messages);
    }
}
