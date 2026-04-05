package fourthargument.eris.api.services;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fourthargument.eris.api.dto.response.PrivateMessageEvent;
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
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public PrivateMessagesDTO sendPrivateMessage(Long conversationId, String content, User sender)
            throws UserException, ConversationException, PrivateMessageException {
        if (content == null || content.isBlank()) {
            throw new PrivateMessageException("Message payload is invalid");
        }

        Conversation conversation = conversationService.getConversationForUser(conversationId, sender.getId());
        PrivateMessage message = privateMessagesMapper.toEntity(content, sender, conversation);
        PrivateMessage saved = privateMessageRepository.save(message);
        PrivateMessagesDTO dto = privateMessagesMapper.toDTO(saved);

        System.out.println("[WS] Broadcast sur /topic/conversation/" + conversationId);

        // ← broadcast
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                new PrivateMessageEvent("NEW", dto));

        System.out.println("[WS] Broadcast terminé");

        return dto;
    }

    public List<PrivateMessagesDTO> getPrivateMessageHistory(Long conversationId, User requester)
            throws ConversationException, PrivateMessageException, UserException {
        Conversation conversation = conversationService.getConversationForUser(conversationId, requester.getId());

        return privateMessageRepository.findByConversationOrderByCreatedAtAsc(conversation).stream()
                .map(privateMessagesMapper::toDTO)
                .toList();
    }

    public PrivateMessagesDTO editPrivateMessage(Long messageId, String content, User requester)
            throws UserException, PrivateMessageException {
        if (content == null || content.isBlank()) {
            throw new PrivateMessageException("Message content cannot be empty");
        }

        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new PrivateMessageException("Private message not found"));

        if (!requester.getId().equals(message.getSender().getId())) {
            throw new PrivateMessageException("You can only edit your own private message");
        }

        message.setContent(content);
        PrivateMessage saved = privateMessageRepository.save(message);
        PrivateMessagesDTO dto = privateMessagesMapper.toDTO(saved);

        // ← broadcast
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.getConversation().getId(),
                new PrivateMessageEvent("EDIT", dto));

        return dto;
    }

    public void deletePrivateMessage(Long messageId, User requester)
            throws UserException, PrivateMessageException {
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new PrivateMessageException("Private message not found"));

        if (!requester.getId().equals(message.getSender().getId())) {
            throw new PrivateMessageException("You can only delete your own private message");
        }

        Long conversationId = message.getConversation().getId();

        privateMessageRepository.delete(message);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                new PrivateMessageEvent("DELETE", messageId));
    }

}
