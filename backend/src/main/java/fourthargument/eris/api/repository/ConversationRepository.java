package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.User;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    List<Conversation> findBySender(User sender);
    List<Conversation> findByReceiver(User receiver);

    void deleteById(Long id);
}
