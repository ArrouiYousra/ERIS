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
                select distinct s
                from Server s
                left join ServerMember sm on sm.server = s
                where s.owner = :user or sm.user = :user
            """)
    List<Server> findAllByUser(@Param("user") User user);

}
