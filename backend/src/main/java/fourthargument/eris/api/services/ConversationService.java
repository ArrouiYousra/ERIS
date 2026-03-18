package fourthargument.eris.api.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.response.ConversationDTO;
import fourthargument.eris.api.mapper.ConversationMapper;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.ConversationRepository;
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
    private final UserService userService;

    public ConversationDTO getOrCreateConversation(String requesterEmail, Long receiverId)
            throws UserException, ConversationException {
        User sender = userService.getUserEntityByEmail(requesterEmail);
        return getOrCreateConversation(sender.getId(), receiverId);
    }

    public ConversationDTO getOrCreateConversation(Long senderId, Long receiverId)
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

        Conversation conversation = conversationRepository.findBySenderAndReceiver(sender, receiver)
                .or(() -> conversationRepository.findBySenderAndReceiver(receiver, sender))
                .orElseGet(() -> conversationRepository.save(conversationMapper.toEntity(sender, receiver)));

        return conversationMapper.toDTO(conversation);
    }

    public List<ConversationDTO> getUserConversations(String requesterEmail) throws UserException {
        User user = userService.getUserEntityByEmail(requesterEmail);

        List<Conversation> all = new ArrayList<>();
        all.addAll(conversationRepository.findBySender(user));
        all.addAll(conversationRepository.findByReceiver(user));

        Map<Long, Conversation> uniq = new LinkedHashMap<>();
        for (Conversation conversation : all) {
            uniq.put(conversation.getId(), conversation);
        }

        return uniq.values().stream()
                .map(conversationMapper::toDTO)
                .toList();
    }

    public Conversation getConversationForUser(Long conversationId, String requesterEmail)
            throws UserException, ConversationException, PrivateMessageException {
        User requester = userService.getUserEntityByEmail(requesterEmail);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationException("Conversation not found"));

        if (!isMember(conversation, requester.getId())) {
            throw new PrivateMessageException("You are not allowed to access this conversation");
        }
        return conversation;
    }

    public void deleteConversation(Long conversationId, String requesterEmail)
            throws UserException, ConversationException, PrivateMessageException {
        Conversation conversation = getConversationForUser(conversationId, requesterEmail);
        conversationRepository.delete(conversation);
    }

    private boolean isMember(Conversation conversation, Long userId) {
        if (conversation == null || userId == null) {
            return false;
        }
        return userId.equals(conversation.getSender().getId()) || userId.equals(conversation.getReceiver().getId());
    }
}
