package Fourth_Argument.eris.api.repository;

import Fourth_Argument.eris.api.model.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {
    List<ServerMember> findByServerId(Long serverId);
    Optional<ServerMember> findByServerIdAndUserId(Long serverId, Long userId);
    boolean existsByServerIdAndUserId(Long serverId, Long userId);
}
