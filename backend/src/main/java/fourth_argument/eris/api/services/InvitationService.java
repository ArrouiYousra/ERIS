package fourth_argument.eris.api.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fourth_argument.eris.api.dto.InvitationDTO;
import fourth_argument.eris.api.dto.response.JoinInviteResponseDTO;
import fourth_argument.eris.api.mapper.InvitationMapper;
import fourth_argument.eris.api.model.Invitation;
import fourth_argument.eris.api.model.Role;
import fourth_argument.eris.api.model.Server;
import fourth_argument.eris.api.model.ServerMember;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.InvitationRepository;
import fourth_argument.eris.api.repository.RoleRepository;
import fourth_argument.eris.api.repository.ServerMemberRepository;
import fourth_argument.eris.api.repository.ServerRepository;
import fourth_argument.eris.api.repository.UserRepository;
import fourth_argument.eris.exceptions.RoleException;
import fourth_argument.eris.exceptions.ServerException;
import fourth_argument.eris.exceptions.ServerMemberException;
import fourth_argument.eris.exceptions.UserException;

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

    public InvitationDTO createInvite(String email, Long serverId)
            throws UserException, ServerException, RoleException, ServerMemberException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        ;

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ServerException("Server not found"));

        ServerMember member = serverMemberRepository
                .findByUserAndServer(user, server)
                .orElseThrow(() -> new ServerMemberException("Not a member"));

        String role = member.getRole().getName();

        if (!role.equals("OWNER") && !role.equals("ADMIN")) {
            throw new RoleException("Not allowed to create invites");
        }
        // Chercher une invitation existante non expirée
        Optional<Invitation> existing = invitationRepository.findFirstByServerOrderByCreatedAtDesc(server);
        if (existing.isPresent() && existing.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            return invitationMapper.toDTO(existing.get());
        }

        // Sinon en créer une nouvelle
        String code = generateCode();
        Invitation invite = new Invitation();
        invite.setCode(code);
        invite.setServer(server);
        invite.setCreatedAt(LocalDateTime.now());
        invite.setExpiresAt(LocalDateTime.now().plusDays(1));

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
