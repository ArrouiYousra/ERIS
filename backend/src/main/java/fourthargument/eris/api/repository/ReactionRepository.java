package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Reaction;

public interface ReactionRepository extends CrudRepository<Reaction, Long> {
    List<Reaction> findByMessage(Message message);
}
