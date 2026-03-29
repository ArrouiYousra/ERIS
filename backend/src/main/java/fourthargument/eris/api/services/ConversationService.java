package fourthargument.eris.api.services;

import java.util.List;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.response.ConversationPreviewDTO;
import fourthargument.eris.api.mapper.ConversationMapper;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ConversationRepository;
import fourthargument.eris.api.repository.PrivateMessageRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;
    private final PrivateMessageRepository privateMessageRepository;

    public ConversationPreviewDTO getOrCreateConversation(Long senderId, Long receiverId)
            throws UserException, ConversationException {
        if (senderId == null || receiverId == null) {
            throw new ConversationException("Sender and receiver are required");
        }
        if (senderId.equals(receiverId)) {
            throw new ConversationException("Cannot create a conversation with yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserException("Receiver not found"));

        Conversation conversation = conversationRepository.findByParticipantsContaining(sender)
                .stream()
                .filter(c -> c.getParticipants()
                        .stream().map(User::getId).anyMatch(id -> id.equals(receiverId)))
                .findFirst()
                .orElseGet(() -> conversationRepository.save(conversationMapper
                        .toEntity(sender, receiver)));

        PrivateMessage lastMessage = privateMessageRepository
                .findTopByConversationOrderByCreatedAtDesc(conversation)
                .orElse(null);

        return conversationMapper.toPreviewDTO(conversation, lastMessage);
    }

    public List<ConversationPreviewDTO> getUserConversations(Long userId) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        return conversationRepository.findByParticipantsContaining(user).stream()
                .map(c -> {
                    PrivateMessage lastMessage = privateMessageRepository
                            .findTopByConversationOrderByCreatedAtDesc(c)
                            .orElse(null);
                    return conversationMapper.toPreviewDTO(c, lastMessage);
                })
                .toList();
    }

    public Conversation getConversationForUser(Long conversationId, Long userId)
            throws UserException, ConversationException, PrivateMessageException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationException("Conversation not found"));

        if (!isMember(conversation, user.getId())) {
            throw new PrivateMessageException("You are not allowed to access this conversation");
        }
        return conversation;
    }

    public void deleteConversation(Long conversationId, Long userId)
            throws UserException, ConversationException, PrivateMessageException {
        Conversation conversation = getConversationForUser(conversationId, userId);
        conversationRepository.delete(conversation);
    }

    private boolean isMember(Conversation conversation, Long userId) {
        if (conversation == null || userId == null) {
            return false;
        }
        return conversation.getParticipants().stream().map(User::getId).anyMatch(id -> id.equals(userId));
    }
}
