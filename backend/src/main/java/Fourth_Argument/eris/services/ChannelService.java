package Fourth_Argument.eris.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.mapper.ChannelMapper;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ServerMemberRepository serverMemberRepository;

    public ChannelDTO createChannel(Long serverId, ChannelDTO dto) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Pas de serveur existant"));

        Channel channel = channelMapper.toEntity(dto, server);
        Channel savedChannel = channelRepository.save(channel);

        if (channel.getName().isEmpty()) {
            throw new RuntimeException("Le channel doit avoir un nom !");
        }

        return channelMapper.toDTO(savedChannel);

    }

    public List<ChannelDTO> getChannelByServer(Long serverId) {

        List<Channel> channels = channelRepository.findByServerId(serverId);

        if (channels == null) {
            throw new RuntimeException("Aucun channel dans ce serveur !");
        }

        List<ChannelDTO> dtoList = channels.stream()
                .map(channelMapper::toDTO)
                .toList();

        return dtoList;
    }

    public ChannelDTO findById(Long id) {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pas de channel existant"));

        return channelMapper.toDTO(channel);

    }

    public ChannelDTO update(ChannelDTO dto, Long id) {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pas de channel existant"));

        if (dto.name() != null) {
            channel.setName(dto.name());
        }
        if (dto.topic() != null) {
            channel.setTopic(dto.topic());
        }
        if (dto.isPrivate() != null) {
            channel.setIsPrivate(dto.isPrivate());
        }

        Channel updatedChannel = channelRepository.save(channel);

        return channelMapper.toDTO(updatedChannel);
    }

    public void delete(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pas de channel existant"));

        User user = userService.getUserEntityByEmail(email);

        // 3️⃣ Get server
        Server server = channel.getServer();

        // 4️⃣ Get server membership
        ServerMember member = serverMemberRepository
                .findByUserAndServer(user, server)
                .orElseThrow(() -> new RuntimeException("User not member of this server"));

        // 5️⃣ Get role
        String roleName = member.getRole().getName();

        // 6️⃣ Check permission
        if (!roleName.equals("OWNER") && !roleName.equals("ADMIN")) {
            throw new RuntimeException("You are not allowed to delete channels");
        }

        List<Message> messages = messageRepository.findByChannel(channel);

        messageRepository.deleteAll(messages);

        channelRepository.delete(channel);

    }
}
