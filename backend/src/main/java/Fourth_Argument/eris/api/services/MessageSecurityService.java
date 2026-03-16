package Fourth_Argument.eris.api.services;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.model.Message;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.MessageRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import lombok.RequiredArgsConstructor;

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

    public boolean canEditMessage(Long messageId, String userEmail) {
        try {
            User user = userService.getUserEntityByEmail(userEmail);
            Message message = messageRepository.findById(messageId).orElse(null);

            if (message == null || user == null) {
                return false;
            }

            Server server = message.getChannel().getServer();
            ServerMember serverMember = serverMemberRepository.findServerMemberByUserAndServer(user, server);

            if (serverMember == null) {
                return false;
            }

            String ServerMemberMail = serverMember.getUser().getEmail();

            if (message.getSender().getEmail().equals(ServerMemberMail)) {
                return true;
            }
            ;

            String roleName = serverMember.getRole().getName();
            return "ADMIN".equals(roleName) || "OWNER".equals(roleName);

        } catch (Exception e) {
            return false;
        }
    }
}