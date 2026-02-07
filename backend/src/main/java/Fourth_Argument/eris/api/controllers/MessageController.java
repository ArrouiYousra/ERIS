package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Fourth_Argument.eris.api.dto.MessageDTO;
import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ChannelRepository;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.MessageException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.MessageService;
import Fourth_Argument.eris.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final MessageRepository messageRepository;

    @PostMapping("/channels/{id}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO req,
            @PathVariable Long id) throws ChannelException, UserException {

        MessageDTO message = messageService.sendMessage(req, id);

        return ResponseEntity.ok(message);

    }

    @GetMapping("/channels/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessageHistory(
            @PathVariable Long id) throws MessageException {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new MessageException("Ce channel n'existe pas"));

        List<MessageDTO> messages = messageService.getMessageHistory(channel);

        return ResponseEntity.ok(messages);

    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails)
            throws MessageException, ServerException, UserException {

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

        ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);
        String roleName = serverMember.getRole().getName();

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageException("Pas de message trouvé"));

        User sender = message.getSender();
        if (sender != user) {
            if (roleName != "OWNER" && roleName != "ADMIN") {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't delete this message");
            }
        }

        messageService.deleteMessage(message);

        return ResponseEntity.ok("Message supprimé !");

    }

}
