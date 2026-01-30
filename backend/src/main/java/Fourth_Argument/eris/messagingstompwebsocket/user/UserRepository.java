package Fourth_Argument.eris.messagingstompwebsocket.user;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAllByStatus(UserStatus online);

}
