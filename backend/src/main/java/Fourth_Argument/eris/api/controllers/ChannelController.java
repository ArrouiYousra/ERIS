package Fourth_Argument.eris.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.ChannelDTO;
import Fourth_Argument.eris.services.ChannelService;

@RestController
public class ChannelController {

    private ChannelService channelService;

    @PostMapping("/servers/{serverId}/channels")
    public ResponseEntity<ChannelDTO> createChannel(ChannelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createChannel(dto));
    }

}
