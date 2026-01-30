package Fourth_Argument.eris.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.services.ChannelService;
import Fourth_Argument.eris.services.UserService;

@RestController
@RequestMapping("/api")
public class ChannelController {

    private ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("/servers/{serverId}/channels")
    public ResponseEntity<String> createChannel(@PathVariable Long serverId,
            @RequestBody ChannelDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Channel created");
    }

}
