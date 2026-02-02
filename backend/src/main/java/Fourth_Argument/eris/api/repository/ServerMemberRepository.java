package Fourth_Argument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Fourth_Argument.eris.api.model.ServerMember;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {
    List<ServerMember> findServerMemberByUserId(Long userId);
    List<ServerMember> findServerMemberByServerId(Long serverId);
    ServerMember findServerMemberByUserIdAndServerId(Long userId, Long serverId);
}
