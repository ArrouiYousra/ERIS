package Fourth_Argument.eris.messagingstompwebsocket.user;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserWsRepository extends MongoRepository<UserWs, String> {

    List<UserWs> findAllByStatus(UserWsStatus online);

}
