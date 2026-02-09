package Fourth_Argument.eris.api.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.ServerMemberService;
import Fourth_Argument.eris.services.ServerService;
import Fourth_Argument.eris.services.UserService;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerService serverService;
    private final ServerMemberService serverMemberService;
    private final UserService userService;

    public ServerController(ServerService serverService, ServerMemberService serverMemberService,
            UserService userService, ServerRepository serverRepository, ServerMemberRepository serverMemberRepository) {
        this.serverService = serverService;
        this.serverMemberService = serverMemberService;
        this.userService = userService;
    }

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

    @GetMapping("/servers/{serverId}/members")
    @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#serverId, authentication.name)")
    public ResponseEntity<List<ServerMemberDTO>> getServerMembers(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserDetails userDetails) throws ServerException, UserException {

        List<ServerMemberDTO> members = serverMemberService.getMembersByServerId(serverId);

        return ResponseEntity.ok(members);
    }

    // @DeleteMapping("/{id}")
    // @PreAuthorize("isAuthenticated() and @serverSecurityService.isServerOwner(#id, authentication.name)")
    // public ResponseEntity<String> deleteServer(@PathVariable Long id)
    //         throws ChannelException, ServerException, ServerMemberException {
    //     serverService.deleteServer(id);
    //     return ResponseEntity.status(HttpStatus.OK).body("Server deleted");
    // }

    // @PostMapping("/{id}/join")
    // @PreAuthorize("isAuthenticated()")
    // public ResponseEntity<String> joinServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO)
    //         throws ServerMemberException {
    //     serverMemberService.createServerMember(id, userDTO.getId());
    //     return ResponseEntity.status(HttpStatus.CREATED).body("Server joined");
    // }

    // @DeleteMapping("/{id}/leave")
    // @PreAuthorize("isAuthenticated() and @serverSecurityService.isMemberOfServer(#id, authentication.name)")
    // public ResponseEntity<String> leaveServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO)
    //         throws ServerException, ServerMemberException {
    //     Long userId = userDTO.getId();
    //     if (Objects.equals(serverService.getServerById(id).getOwnerId(), userId)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The owner cannot leave the server");
    //     }
    //     serverMemberService.deleteServerMember(id, userId);
    //     return ResponseEntity.status(HttpStatus.OK).body("Server left");
    // }
}