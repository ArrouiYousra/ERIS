package fourth_argument.eris.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fourth_argument.eris.api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
