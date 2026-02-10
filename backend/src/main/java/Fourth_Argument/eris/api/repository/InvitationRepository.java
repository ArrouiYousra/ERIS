package Fourth_Argument.eris.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Fourth_Argument.eris.api.model.Invitation;
import Fourth_Argument.eris.api.model.Server;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByCode(String code);

    Optional<Invitation> findFirstByServerOrderByCreatedAtDesc(Server server);

}
