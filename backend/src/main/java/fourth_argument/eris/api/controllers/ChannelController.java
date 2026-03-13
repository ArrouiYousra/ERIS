package fourth_argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourth_argument.eris.api.dto.ChannelDTO;
import fourth_argument.eris.api.services.ChannelService;
import fourth_argument.eris.exceptions.ChannelException;
import fourth_argument.eris.exceptions.ServerException;
import fourth_argument.eris.exceptions.UserException;

@RestController
@RequestMapping("/api")
public class ChannelController {

    private ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("/servers/{serverId}/channels")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isServerAdmin(#serverId, authentication.name)")
    public ResponseEntity<ChannelDTO> createChannel(@PathVariable Long serverId,
            @RequestBody ChannelDTO dto) throws ChannelException, ServerException {

        ChannelDTO createdChannel = channelService.createChannel(serverId, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @GetMapping("/servers/{serverId}/channels")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#serverId, authentication.name)")
    public ResponseEntity<List<ChannelDTO>> getChannelByServer(@PathVariable Long serverId) throws ChannelException {
        List<ChannelDTO> channels = channelService.getChannelByServer(serverId);

        return ResponseEntity.ok(channels);
    }

    @GetMapping("/channels/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfChannel(#id, authentication.name)")
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable Long id) throws ChannelException {
        ChannelDTO channel = channelService.findById(id);

        return ResponseEntity.ok(channel);
    }

    @PutMapping("/channels/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isChannelAdmin(#id, authentication.name)")
    public ResponseEntity<ChannelDTO> update(@PathVariable Long id, @RequestBody ChannelDTO dto)
            throws ChannelException {

        ChannelDTO updatedChannel = channelService.update(dto, id);

        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/channels/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isChannelAdmin(#id, authentication.name)")
    public ResponseEntity<String> delete(@PathVariable Long id) throws ChannelException, UserException {

        channelService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Le channel a été supprimé !");
    }

}
