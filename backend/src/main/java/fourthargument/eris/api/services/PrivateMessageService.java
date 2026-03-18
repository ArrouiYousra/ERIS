package fourthargument.eris.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.response.PrivateMessagesDTO;
import fourthargument.eris.api.mapper.PrivateMessagesMapper;
import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.PrivateMessageRepository;
import fourthargument.eris.exceptions.ConversationException;
import fourthargument.eris.exceptions.PrivateMessageException;
import fourthargument.eris.exceptions.UserException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrivateMessageService {

    private final PrivateMessageRepository privateMessageRepository;
    private final PrivateMessagesMapper privateMessagesMapper;
    private final UserService userService;
    private final ConversationService conversationService;

    public PrivateMessagesDTO sendPrivateMessage(Long conversationId, String content, String requesterEmail)
            throws UserException, ConversationException, PrivateMessageException {
        if (content == null || content.isBlank()) {
            throw new PrivateMessageException("Message payload is invalid");
        }

        User sender = userService.getUserEntityByEmail(requesterEmail);
        Conversation conversation = conversationService.getConversationForUser(conversationId, requesterEmail);
        PrivateMessagesDTO dto = new PrivateMessagesDTO(
                null,
                sender.getId(),
                sender.getUser(),
                conversation.getReceiver().getId(),
                conversation.getReceiver().getUser(),
                conversationId,
                content,
                null,
                null);
        PrivateMessage message = privateMessagesMapper.toEntity(dto, sender, conversation);
        PrivateMessage saved = privateMessageRepository.save(message);
        return privateMessagesMapper.toDTO(saved);
    }

    public List<PrivateMessagesDTO> getPrivateMessageHistory(Long conversationId, String requesterEmail)
            throws ConversationException, PrivateMessageException, UserException {
        Conversation conversation = conversationService.getConversationForUser(conversationId, requesterEmail);

        return privateMessageRepository.findByConversationOrderByCreatedAtAsc(conversation).stream()
                .map(privateMessagesMapper::toDTO)
                .toList();
    }

    public PrivateMessagesDTO editPrivateMessage(Long messageId, String content, String requesterEmail)
            throws UserException, PrivateMessageException {
        if (content == null || content.isBlank()) {
            throw new PrivateMessageException("Message content cannot be empty");
        }

        User requester = userService.getUserEntityByEmail(requesterEmail);
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new PrivateMessageException("Private message not found"));

        if (!requester.getId().equals(message.getSender().getId())) {
            throw new PrivateMessageException("You can only edit your own private message");
        }

        message.setContent(content);
        PrivateMessage saved = privateMessageRepository.save(message);
        return privateMessagesMapper.toDTO(saved);
    }

    public void deletePrivateMessage(Long messageId, String requesterEmail)
            throws UserException, PrivateMessageException {
        User requester = userService.getUserEntityByEmail(requesterEmail);
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new PrivateMessageException("Private message not found"));

        if (!requester.getId().equals(message.getSender().getId())) {
            throw new PrivateMessageException("You can only delete your own private message");
        }

        privateMessageRepository.delete(message);
    }

}
