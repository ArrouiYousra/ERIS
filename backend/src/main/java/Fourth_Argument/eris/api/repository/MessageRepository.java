package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import Fourth_Argument.eris.api.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByChannel(Long channelId);

}
