package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByChannel(Channel channel);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.channel.id IN (SELECT c.id FROM Channel c WHERE c.server.id = :serverId)")
    void deleteAllByServerId(Long serverId);

}
