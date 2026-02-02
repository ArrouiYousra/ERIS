package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.services.MessageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ChannelRepository channelRepository;

    @PostMapping("/channels/{id}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO req,
            @PathVariable Long id) {

        MessageDTO message = messageService.sendMessage(req, id);

        return ResponseEntity.ok(message);

    }

    @GetMapping("/channels/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessageHistory(
            @PathVariable Long id) {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ce channel n'existe pas"));

        List<MessageDTO> messages = messageService.getMessageHistory(channel);

        return ResponseEntity.ok(messages);

    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long id) {

        messageService.deleteMessage(id);

        return ResponseEntity.ok("Message supprimé !");

    }

}
