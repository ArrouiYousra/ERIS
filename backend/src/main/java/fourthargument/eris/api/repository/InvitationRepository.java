package fourthargument.eris.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import fourthargument.eris.api.model.Invitation;
import fourthargument.eris.api.model.Server;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByCode(String code);

    Optional<Invitation> findFirstByServerOrderByCreatedAtDesc(Server server);

    @Modifying
    @Transactional
    @Query("DELETE FROM Invitation i WHERE i.server.id = :serverId")
    void deleteAllByServerId(Long serverId);

}
