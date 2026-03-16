package fourthargument.eris.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

    List<ServerMember> findByServer(Server server);

    ServerMember findServerMemberByUserAndServer(User user, Server server);

    List<ServerMember> findServerMembersByServer(Server server, User user);

    boolean existsByUserAndServer(User user, Server server);

    List<ServerMember> findServerMemberByUserId(Long id);

    Optional<ServerMember> findByUserAndServer(User user, Server server);

    @Modifying
    @Transactional
    @Query("DELETE FROM ServerMember sm WHERE sm.server.id = :serverId")
    void deleteAllByServerId(Long serverId);
}
