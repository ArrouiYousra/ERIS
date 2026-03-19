package fourthargument.eris.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;
import fourthargument.eris.api.model.User;

public interface PrivateMessageRepository extends CrudRepository<PrivateMessage, Long> {
    List<PrivateMessage> findByConversation(Conversation conversation);
    List<PrivateMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    List<PrivateMessage> findBySender(User sender);
    Optional<PrivateMessage> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);
}
