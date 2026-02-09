package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.MessageException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.MessageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ChannelRepository channelRepository;

    @PostMapping("/channels/{id}/messages")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#id, authentication.name)")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO req,
            @PathVariable Long id) throws ChannelException, UserException {

        MessageDTO message = messageService.sendMessage(req, id);

        return ResponseEntity.ok(message);

    }

    @GetMapping("/channels/{id}/messages")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#id, authentication.name)")
    public ResponseEntity<List<MessageDTO>> getMessageHistory(
            @PathVariable Long id) throws MessageException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new MessageException("Ce channel n'existe pas"));

        List<MessageDTO> messages = messageService.getMessageHistory(channel);

        return ResponseEntity.ok(messages);

    }

    @DeleteMapping("/messages/{id}")
    @PreAuthorize("isAuthenticated() and @messageSecurityService.canDeleteMessage(#id, authentication.name)")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long id) throws MessageException {

        messageService.deleteMessage(id);

        return ResponseEntity.ok("Message supprimé !");

    }

}
