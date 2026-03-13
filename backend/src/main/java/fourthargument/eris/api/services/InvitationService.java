package fourthargument.eris.api.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.InvitationDTO;
import fourthargument.eris.api.dto.response.JoinInviteResponseDTO;
import fourthargument.eris.api.mapper.InvitationMapper;
import fourthargument.eris.api.model.Invitation;
import fourthargument.eris.api.model.Role;
import fourthargument.eris.api.model.Server;
import fourthargument.eris.api.model.ServerMember;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.InvitationRepository;
import fourthargument.eris.api.repository.RoleRepository;
import fourthargument.eris.api.repository.ServerMemberRepository;
import fourthargument.eris.api.repository.ServerRepository;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.RoleException;
import fourthargument.eris.exceptions.ServerException;
import fourthargument.eris.exceptions.ServerMemberException;
import fourthargument.eris.exceptions.UserException;

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
