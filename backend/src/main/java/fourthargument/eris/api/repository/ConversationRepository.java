package fourthargument.eris.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Conversation;
import fourthargument.eris.api.model.User;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    List<Conversation> findBySender(User sender);
    List<Conversation> findByReceiver(User receiver);
    Optional<Conversation> findBySenderAndReceiver(User sender, User receiver); // Création d'une conv unique

    void deleteById(Long id);
}
