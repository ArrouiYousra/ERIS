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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourthargument.eris.api.dto.request.CreateConversationRequestDTO;
import fourthargument.eris.api.dto.response.ConversationPreviewDTO;
import fourthargument.eris.api.services.ConversationService;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.UserException;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConversationPreviewDTO> createOrGetConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConversationRequestDTO request)
            throws UserException, ConversationException {
        User requester = userService.getUserEntityByEmail(userDetails.getUsername());
        ConversationPreviewDTO conversation = conversationService.getOrCreateConversation(
            requester.getId(),
            request.getReceiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConversationPreviewDTO>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails)
            throws UserException {
        User requester = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(conversationService.getUserConversations(requester.getId()));
    }

    @DeleteMapping("/{conversationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long conversationId)
            throws UserException, ConversationException, PrivateMessageException {
        User requester = userService.getUserEntityByEmail(userDetails.getUsername());
        conversationService.deleteConversation(conversationId, requester.getId());
        return ResponseEntity.ok("Conversation deleted");
    }
}
