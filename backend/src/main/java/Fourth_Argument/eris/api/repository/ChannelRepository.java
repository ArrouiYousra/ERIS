package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import Fourth_Argument.eris.api.model.Channel;
import Fourth_Argument.eris.api.model.Server;

public interface ChannelRepository extends CrudRepository<Channel, Long> {

    @Query(value = "SELECT * FROM channel WHERE server_id=?1", nativeQuery = true)
    List<Channel> findByServerId(Long serverId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Channel c WHERE c.server.id = :serverId")
    void deleteAllByServerId(Long serverId);

    List<Channel> getChannelsByServer(Server server);

}
