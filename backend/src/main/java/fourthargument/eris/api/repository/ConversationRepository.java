package fourthargument.eris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fourthargument.eris.api.model.User;
import fourthargument.eris.api.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByParticipantsContaining(User user);

    @Query(value = "SELECT user_id FROM conversation_participants WHERE conversation_id = ?1", nativeQuery = true)
    List<Long> findParticipantIdsByConversationId(Long conversationId); // récupère les ids des participants d'une conversation
}
