package fourthargument.eris.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.model.Message;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.MessageRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;

@Service("messageSecurityService")
@RequiredArgsConstructor
public class MessageSecurityService {

    private final MessageRepository messageRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final UserService userService;

    public boolean canDeleteMessage(Long messageId, String userEmail) {
        try {
            User user = userService.getUserEntityByEmail(userEmail);
            Message message = messageRepository.findById(messageId).orElse(null);

            if (message == null || user == null) {
                return false;
            }

            if (message.getSender().getId().equals(user.getId())) {
                return true;
            }

            Server server = message.getChannel().getServer();
            ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

            if (serverMember == null) {
                return false;
            }

            String roleName = serverMember.getRole().getName();
            return "ADMIN".equals(roleName) || "OWNER".equals(roleName);

        } catch (Exception e) {
            return false;
        }
    }
}