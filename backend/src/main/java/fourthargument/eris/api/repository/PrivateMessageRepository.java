package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.PrivateMessage;

public interface PrivateMessageRepository extends CrudRepository<PrivateMessage, Long> {
    List<PrivateMessage> findByConversation(Conversation conversation);
    List<PrivateMessage> findBySender(User sender);

    void deleteById(Long id);
}
