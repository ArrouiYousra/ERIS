package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

    List<ServerMember> findByServer(Server server);

    ServerMember findServerMemberByUserAndServer(Server server, User user);

    List<ServerMember> findServerMembersByServer(Server server, User user);

    boolean existsByUserAndServer(User user, Server server);
}
