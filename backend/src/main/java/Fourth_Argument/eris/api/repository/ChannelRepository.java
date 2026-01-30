package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import Fourth_Argument.eris.api.model.Channel;

public interface ChannelRepository extends CrudRepository<Channel, Long> {

    @Query(value = "SELECT * FROM channel WHERE server_id=?1", nativeQuery = true)
    List<Channel> findByServerId(Long serverId);

}
