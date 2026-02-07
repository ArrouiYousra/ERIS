package Fourth_Argument.eris.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

    List<ServerMember> findByServer(Server server);

    ServerMember findServerMemberByUserAndServer(User user, Server server);

    List<ServerMember> findServerMembersByServer(Server server, User user);

    boolean existsByUserAndServer(User user, Server server);

    List<ServerMember> findServerMemberByUserId(Long id);

    Optional<ServerMember> findByUserAndServer(User user, Server server);
}
