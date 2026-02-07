package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.ChannelService;

@RestController
@RequestMapping("/api")
public class ChannelController {

    private ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("/servers/{serverId}/channels")
    public ResponseEntity<ChannelDTO> createChannel(@PathVariable Long serverId,
            @RequestBody ChannelDTO dto) throws ChannelException, ServerException {

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
    public ResponseEntity<ChannelDTO> update(@PathVariable Long id, @RequestBody ChannelDTO dto)
            throws ChannelException {

        ChannelDTO updatedChannel = channelService.update(dto, id);

        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/channels/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws ChannelException, UserException {

        channelService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Le channel a été supprimé !");
    }

}
