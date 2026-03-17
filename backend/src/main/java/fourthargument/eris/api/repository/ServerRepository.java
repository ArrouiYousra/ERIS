package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.User;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    List<Server> findByOwner(User user);

    @Query("""
                SELECT DISTINCT s FROM Server s
                LEFT JOIN ServerMember sm ON sm.server = s
                WHERE (s.owner = :user OR sm.user = :user)
                AND NOT EXISTS (
                    SELECT 1 FROM BannedMember bm
                    WHERE bm.user = :user AND bm.server = s
                    AND (bm.expiresAt IS NULL OR bm.expiresAt > CURRENT_TIMESTAMP)
                )
            """)
    List<Server> findAllByUser(@Param("user") User user);   

}
