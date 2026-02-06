package Fourth_Argument.eris.api.repository;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    List<Server> findByOwner(User user);

}
