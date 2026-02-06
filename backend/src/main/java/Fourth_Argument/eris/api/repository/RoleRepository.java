package Fourth_Argument.eris.api.repository;

import org.springframework.stereotype.Repository;

import Fourth_Argument.eris.api.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
