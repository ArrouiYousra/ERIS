package Fourth_Argument.eris.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.InvitationDTO;
import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.dto.request.JoinInviteRequestDTO;
import Fourth_Argument.eris.api.dto.response.JoinInviteResponseDTO;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.exceptions.RoleException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.InvitationService;
import Fourth_Argument.eris.services.ServerMemberService;
import Fourth_Argument.eris.services.ServerService;
import Fourth_Argument.eris.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final ServerMemberService serverMemberService;
    private final UserService userService;
    private final InvitationService invitationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerDTO> createServer(
            @RequestBody ServerDTO serverDTO,
            @AuthenticationPrincipal UserDetails userDetails) throws UserException {

        String email = userDetails.getUsername();
        User currentUser = userService.getUserEntityByEmail(email);

        ServerDTO createdServer = serverService.createServer(serverDTO, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdServer);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServerDTO>> getUserServers(@AuthenticationPrincipal UserDetails userDetails)
            throws ServerException, UserException {

        String email = userDetails.getUsername();
        User currentUser = userService.getUserEntityByEmail(email);

        List<ServerDTO> servers = serverService.getServersByUser(currentUser);

        return ResponseEntity.ok(servers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#id, authentication.name)")
    public ResponseEntity<ServerDTO> getServer(@PathVariable Long id) throws ServerException {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isServerOwner(#id, authentication.name)")
    public ResponseEntity<String> updateServer(@PathVariable Long id, @RequestBody ServerDTO serverDTO,
            @AuthenticationPrincipal UserDetails userDetails) throws ServerException, UserException {

        serverService.updateServer(id, serverDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("Server updated");
    }

    @GetMapping("/{serverId}/members")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#serverId, authentication.name)")
    public ResponseEntity<List<ServerMemberDTO>> getServerMembers(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserDetails userDetails) throws ServerException, UserException {

        List<ServerMemberDTO> members = serverMemberService.getMembersByServerId(serverId);

        return ResponseEntity.ok(members);
    }

    @PostMapping("/{serverId}/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvitationDTO> createInvitation(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long serverId) throws UserException, ServerException, RoleException, ServerMemberException {

        String email = userDetails.getUsername();
        InvitationDTO dto = invitationService.createInvite(email, serverId);

        return ResponseEntity.ok(dto);

    }

    @PostMapping("/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JoinInviteResponseDTO> joinServer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody JoinInviteRequestDTO request) throws UserException, ServerMemberException {

        String email = userDetails.getUsername();
        JoinInviteResponseDTO response = invitationService.joinServerWithInvite(email, request.getCode());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isServerOwner(#id, authentication.name)")
    public ResponseEntity<String> deleteServer(@PathVariable Long id) throws ServerException {
        serverService.deleteServer(id);
        return ResponseEntity.status(HttpStatus.OK).body("Server deleted");
    }

    @DeleteMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#id, authentication.name) and !@serverSecurityService.isServerOwner(#id, authentication.name)")
    public ResponseEntity<String> leaveServer(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id)
            throws ServerException, ServerMemberException, UserException {

        String email = userDetails.getUsername();
        serverMemberService.deleteServerMember(email, id);

        return ResponseEntity.status(HttpStatus.OK).body("Server left");
    }
}