package Fourth_Argument.eris.api.repository;

import Fourth_Argument.eris.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByServerIdAndName(Long serverId, String name);
}
