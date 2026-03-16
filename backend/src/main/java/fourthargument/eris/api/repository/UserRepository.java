<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/repository/UserRepository.java
package fourthargument.eris.api.repository;
========
package fourth_argument.eris.api.repository;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/repository/UserRepository.java

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/repository/UserRepository.java
import fourthargument.eris.api.model.User;
========
import fourth_argument.eris.api.model.User;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/repository/UserRepository.java

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
