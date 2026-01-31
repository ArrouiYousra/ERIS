package Fourth_Argument.eris.api.repository;

import org.springframework.data.repository.CrudRepository;

import Fourth_Argument.eris.api.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

}
