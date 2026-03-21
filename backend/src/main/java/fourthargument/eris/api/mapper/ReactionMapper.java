package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.request.ReactionRequestDTO;
import fourthargument.eris.api.dto.response.ReactionResponseDTO;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Reaction;
import fourthargument.eris.api.model.User;

@Component
public class ReactionMapper {
    public ReactionResponseDTO toDTO(Reaction reaction) {
        return new ReactionResponseDTO(reaction.getUser().getId(), reaction.getMessage().getId(), reaction.getEmoji());
    }

    public Reaction toEntity(ReactionRequestDTO dto, User user, Message message) {
        return new Reaction(user, message, dto.emoji());
    }
}
