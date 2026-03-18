package fourthargument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourthargument.eris.api.dto.request.SendPrivateMessageRequestDTO;
import fourthargument.eris.api.dto.request.UpdatePrivateMessageRequestDTO;
import fourthargument.eris.api.dto.response.PrivateMessagesDTO;
import fourthargument.eris.api.services.PrivateMessageService;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
public class PrivateMessageController {

    private final PrivateMessageService privateMessageService;

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrivateMessagesDTO>> getConversationMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long conversationId)
            throws UserException, ConversationException, PrivateMessageException {
        return ResponseEntity.ok(privateMessageService.getPrivateMessageHistory(conversationId, userDetails.getUsername()));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrivateMessagesDTO> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long conversationId,
            @RequestBody SendPrivateMessageRequestDTO request)
            throws UserException, ConversationException, PrivateMessageException {
        PrivateMessagesDTO sent = privateMessageService.sendPrivateMessage(
                conversationId,
                request.getContent(),
                userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(sent);
    }

    @PutMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrivateMessagesDTO> editMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long messageId,
            @RequestBody UpdatePrivateMessageRequestDTO request)
            throws UserException, PrivateMessageException {
        return ResponseEntity.ok(privateMessageService.editPrivateMessage(messageId, request.getContent(), userDetails.getUsername()));
    }

    @DeleteMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long messageId)
            throws UserException, PrivateMessageException {
        privateMessageService.deletePrivateMessage(messageId, userDetails.getUsername());
        return ResponseEntity.ok("Private message deleted");
    }
}
