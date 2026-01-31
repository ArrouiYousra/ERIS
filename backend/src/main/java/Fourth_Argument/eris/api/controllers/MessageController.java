package Fourth_Argument.eris.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.services.MessageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/channels/{id}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO req,
            @PathVariable Long id) {

        MessageDTO message = messageService.sendMessage(req, id);

        return ResponseEntity.ok(message);

    }

}
