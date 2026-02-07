package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.ChannelService;
import Fourth_Argument.eris.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final UserService userService;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;

    @PostMapping("/servers/{serverId}/channels")
    public ResponseEntity<ChannelDTO> createChannel(@PathVariable Long serverId,
            @RequestBody ChannelDTO dto, @AuthenticationPrincipal UserDetails userDetails)
            throws ChannelException, ServerException, UserException {

        String email = userDetails.getUsername();
        User user = userService.getUserEntityByEmail(email);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> {
                    return new ServerException("Server not found");
                });

        boolean isMember = serverMemberRepository.existsByUserAndServer(user, server);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this server");
        }

        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(server, user);
        String roleName = serverMember.getRole().getName();

        if (roleName != "OWNER" && roleName != "ADMIN") {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't create a channel in this server");
        }

        ChannelDTO createdChannel = channelService.createChannel(serverId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @GetMapping("/servers/{serverId}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannelByServer(@PathVariable Long serverId) throws ChannelException {
        List<ChannelDTO> channels = channelService.getChannelByServer(serverId);

        return ResponseEntity.ok(channels);
    }

    @GetMapping("/channels/{id}")
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable Long id) throws ChannelException {
        ChannelDTO channel = channelService.findById(id);

        return ResponseEntity.ok(channel);
    }

    @PutMapping("/channels/{id}")
    public ResponseEntity<ChannelDTO> update(@PathVariable Long id, @RequestBody ChannelDTO dto,
            @AuthenticationPrincipal UserDetails userDetails)
            throws ChannelException, ServerException, UserException {

        String email = userDetails.getUsername();
        User user = userService.getUserEntityByEmail(email);

        Server server = serverRepository.findById(id)
                .orElseThrow(() -> {
                    return new ServerException("Server not found");
                });

        boolean isMember = serverMemberRepository.existsByUserAndServer(user, server);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this server");
        }

        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(server, user);
        String roleName = serverMember.getRole().getName();

        if (roleName != "OWNER" && roleName != "ADMIN") {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't update channels in this server");
        }

        ChannelDTO updatedChannel = channelService.update(dto, id);

        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/channels/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws ChannelException {

        channelService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Le channel a été supprimé !");
    }

}
