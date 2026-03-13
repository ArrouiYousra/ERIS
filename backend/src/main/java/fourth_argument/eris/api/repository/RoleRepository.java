package fourth_argument.eris.api.repository;

import org.springframework.stereotype.Repository;

import fourth_argument.eris.api.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
