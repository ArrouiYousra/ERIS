package Fourth_Argument.eris.api.repository;

import Fourth_Argument.eris.api.model.Server;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    
}
