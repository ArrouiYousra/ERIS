package Fourth_Argument.eris.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.InvitationDTO;
import Fourth_Argument.eris.api.dto.response.JoinInviteResponseDTO;
import Fourth_Argument.eris.api.mapper.InvitationMapper;
import Fourth_Argument.eris.api.model.Invitation;
import Fourth_Argument.eris.api.model.Role;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.ServerMember;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.InvitationRepository;
import Fourth_Argument.eris.api.repository.RoleRepository;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final UserRepository userRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerMemberService serverMemberService;
    private final UserService userService;
    private final ServerRepository serverRepository;
    private final RoleRepository roleRepository;

    public InvitationService(InvitationRepository invitationRepository, InvitationMapper invitationMapper,
            UserRepository userRepository, ServerMemberRepository serverMemberRepository,
            ServerMemberService serverMemberService, UserService userService,
            ServerRepository serverRepository, RoleRepository roleRepository) {
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
        this.userRepository = userRepository;
        this.serverMemberRepository = serverMemberRepository;
        this.serverRepository = serverRepository;
        this.serverMemberService = serverMemberService;
        this.userService = userService;
        this.roleRepository = roleRepository;

    }

    public String generateCode() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);

    }

    public InvitationDTO createInvite(String email, Long serverId) throws UserException, ServerException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        ;

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        ServerMember member = serverMemberRepository
                .findByUserAndServer(user, server)
                .orElseThrow(() -> new RuntimeException("Not a member"));

        String role = member.getRole().getName();

        // if (!role.equals("OWNER") && !role.equals("ADMIN")) {
        // throw new RuntimeException("Not allowed to create invites");
        // }
        String code = generateCode();

        Invitation invite = new Invitation();
        invite.setCode(code);
        invite.setCode(code);
        invite.setServer(server); // ✅ associate server
        invite.setCreatedAt(LocalDateTime.now()); // ✅ creation date
        invite.setExpiresAt(LocalDateTime.now().plusDays(1)); // ✅ example: 7-day expiration

        Invitation saved = invitationRepository.save(invite);

        return invitationMapper.toDTO(saved);

    }

    public JoinInviteResponseDTO joinServerWithInvite(String email, String code)
            throws UserException, ServerMemberException {

        User user = userService.getUserEntityByEmail(email);

        Invitation invite = invitationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invite expired");
        }

        Role role = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Role MEMBER not found"));

        // ✅ Add user as MEMBER
        serverMemberService.createServerMember(invite.getServer(), user, role);

        // ✅ Return response DTO
        JoinInviteResponseDTO response = new JoinInviteResponseDTO();
        response.setServerId(invite.getServer().getId());
        response.setServerName(invite.getServer().getName());
        response.setMessage("Joined successfully");

        return response;
    }

}
