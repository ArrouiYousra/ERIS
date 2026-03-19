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
import fourthargument.eris.api.dto.response.ConversationDTO;
import fourthargument.eris.api.services.ConversationService;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConversationDTO> createOrGetConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConversationRequestDTO request)
            throws UserException, ConversationException {
        ConversationDTO conversation = conversationService.getOrCreateConversation(
                userDetails.getUsername(),
                request.getReceiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConversationDTO>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails)
            throws UserException {
        return ResponseEntity.ok(conversationService.getUserConversations(userDetails.getUsername()));
    }

    @DeleteMapping("/{conversationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long conversationId)
            throws UserException, ConversationException, PrivateMessageException {
        conversationService.deleteConversation(conversationId, userDetails.getUsername());
        return ResponseEntity.ok("Conversation deleted");
    }
}
