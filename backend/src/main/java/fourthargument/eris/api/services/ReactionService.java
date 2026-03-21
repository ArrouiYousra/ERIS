package fourthargument.eris.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.request.ReactionRequestDTO;
import fourthargument.eris.api.dto.response.ReactionResponseDTO;
import fourthargument.eris.api.mapper.ReactionMapper;
import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Reaction;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.ReactionRepository;
import fourthargument.eris.exceptions.MessageException;
import fourthargument.eris.exceptions.ReactionException;
import fourthargument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public ReactionResponseDTO createReaction(String email, ReactionRequestDTO dto) throws UserException, MessageException {
        User user = userService.getUserEntityByEmail(email);
        Message message = messageRepository.findById(dto.messageId())
                .orElseThrow(() -> new MessageException("Message not found"));

        Reaction reaction = reactionMapper.toEntity(dto, user, message);
        Reaction savedReaction = reactionRepository.save(reaction);
        ReactionResponseDTO newDTO = reactionMapper.toDTO(savedReaction);

        messagingTemplate.convertAndSend("/topic/reactions",
                (Object) Map.of("type", "CREATED", "object", newDTO));

        return newDTO;
    }

    public List<ReactionResponseDTO> getReactionsByMessage(Long id) throws MessageException {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageException("Message not found"));

        List<Reaction> reactions = reactionRepository.findByMessage(message);
        List<ReactionResponseDTO> dtos = reactions.stream()
                .map(reaction -> reactionMapper.toDTO(reaction))
                .toList();

        return dtos;
    }

    public ReactionResponseDTO updateReaction(String email, ReactionRequestDTO dto)
            throws ReactionException, UserException, MessageException {
        User user = userService.getUserEntityByEmail(email);
        Message message = messageRepository.findById(dto.messageId())
                .orElseThrow(() -> new MessageException("Message not found"));

        Reaction reaction = reactionRepository.findByUserAndMessage(user, message);
        reaction.setEmoji(dto.emoji());
        Reaction savedReaction = reactionRepository.save(reaction);
        ReactionResponseDTO newDTO = reactionMapper.toDTO(savedReaction);

        messagingTemplate.convertAndSend("/topic/reactions",
                (Object) Map.of("type", "UPDATED", "object", newDTO));

        return newDTO;
    }

    public void deleteReaction(String email, Long id) throws UserException, MessageException {
        User user = userService.getUserEntityByEmail(email);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageException("Message not found"));
        Reaction reaction = reactionRepository.findByUserAndMessage(user, message);

        reactionRepository.delete(reaction);

        messagingTemplate.convertAndSend("/topic/reactions",
                (Object) Map.of("type", "DELETED"));
    }
}
