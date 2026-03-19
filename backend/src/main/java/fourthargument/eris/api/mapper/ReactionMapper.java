package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.ReactionDTO;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Reaction;
import fourthargument.eris.api.model.User;

@Component
public class ReactionMapper {
    public ReactionDTO toDTO(Reaction reaction) {
        return new ReactionDTO(reaction.getUser().getId(), reaction.getMessage().getId(), reaction.getEmoji());
    }

    public Reaction toEntity(ReactionDTO dto, User user, Message message) {
        return new Reaction(user, message, dto.emoji());
    }
}
