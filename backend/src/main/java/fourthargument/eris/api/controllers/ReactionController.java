package fourthargument.eris.api.controllers;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourthargument.eris.api.dto.ReactionDTO;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.ReactionService;
import fourthargument.eris.api.services.UserService;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReactionDTO> createReaction(@AuthenticationPrincipal UserDetails userDetails) {
        // String email = userDetails.getUsername();
        // User currentUser = userService.getUserEntityByEmail(email);
        // ReactionDTO reactionDTO = reactionService.createReaction();
        throw new NotImplementedException();
    };

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReactionDTO> getReaction(@AuthenticationPrincipal UserDetails userDetails) {
        throw new NotImplementedException();
    };

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReactionDTO> updateReaction(@AuthenticationPrincipal UserDetails userDetails,
            ReactionDTO reactionDTO) {
        throw new NotImplementedException();
    };

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteReaction(@AuthenticationPrincipal UserDetails userDetails,
            ReactionDTO reactionDTO) {
        throw new NotImplementedException();
    };
}
