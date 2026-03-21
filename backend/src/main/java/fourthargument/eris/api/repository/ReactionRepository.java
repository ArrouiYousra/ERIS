package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Reaction;
import fourthargument.eris.api.model.User;

public interface ReactionRepository extends CrudRepository<Reaction, Long> {
    List<Reaction> findByMessage(Message message);
    Reaction findByUserAndMessage(User user, Message message);
}
