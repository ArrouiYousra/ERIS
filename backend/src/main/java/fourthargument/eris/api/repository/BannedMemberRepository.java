package fourthargument.eris.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fourthargument.eris.api.model.BannedMember;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;

@Repository
public interface BannedMemberRepository extends JpaRepository<BannedMember, Long> {

    Optional<BannedMember> findByUserAndServer(User user, Server server);

    List<BannedMember> findByServer(Server server);

    List<BannedMember> findByUser(User user);

    boolean existsByUserAndServer(User user, Server server);

    @Query("""
        SELECT CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
        FROM BannedMember bm
        WHERE bm.user = :user AND bm.server = :server
        AND (bm.expiresAt IS NULL OR bm.expiresAt > CURRENT_TIMESTAMP)
    """)
    boolean isCurrentlyBanned(@Param("user") User user, @Param("server") Server server);
}
