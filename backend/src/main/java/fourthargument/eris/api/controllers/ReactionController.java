package fourthargument.eris.api.controllers;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourthargument.eris.api.dto.request.ReactionRequestDTO;
import fourthargument.eris.api.dto.response.ReactionResponseDTO;
import fourthargument.eris.api.services.ReactionService;
import fourthargument.eris.exceptions.MessageException;
import fourthargument.eris.exceptions.ReactionException;
import fourthargument.eris.exceptions.UserException;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReactionResponseDTO> createReaction(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReactionRequestDTO dto)
            throws UserException, MessageException {

        String email = userDetails.getUsername();
        ReactionResponseDTO newDTO = reactionService.createReaction(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDTO);
    };

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReactionResponseDTO>> getReactionsByMessage(@PathVariable Long id) throws MessageException {

        List<ReactionResponseDTO> reactions = reactionService.getReactionsByMessage(id);
        return ResponseEntity.ok(reactions);
    };

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReactionResponseDTO> updateReaction(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReactionRequestDTO dto) throws ReactionException, UserException, MessageException {

        String email = userDetails.getUsername();
        ReactionResponseDTO newDTO = reactionService.updateReaction(email, dto);
        return ResponseEntity.ok(newDTO);
    };

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteReaction(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) throws UserException, MessageException {

        String email = userDetails.getUsername();
        reactionService.deleteReaction(email, id);
        return ResponseEntity.status(HttpStatus.OK).body("Reaction deleted");
    };
}
