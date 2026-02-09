package Fourth_Argument.eris.api.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.model.Server;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.ServerMemberRepository;
import Fourth_Argument.eris.api.repository.ServerRepository;
import Fourth_Argument.eris.exceptions.ChannelException;
import Fourth_Argument.eris.exceptions.ServerException;
import Fourth_Argument.eris.exceptions.ServerMemberException;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.ServerMemberService;
import Fourth_Argument.eris.services.ServerService;
import Fourth_Argument.eris.services.UserService;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerService serverService;
    private final ServerMemberService serverMemberService;
    private final ServerRepository serverRepository;
    private final UserService userService;
    private final ServerMemberRepository serverMemberRepository;

    public ServerController(ServerService serverService, ServerMemberService serverMemberService,
            UserService userService, ServerRepository serverRepository, ServerMemberRepository serverMemberRepository) {
        this.serverService = serverService;
        this.serverMemberService = serverMemberService;
        this.serverMemberRepository = serverMemberRepository;
        this.userService = userService;
        this.serverRepository = serverRepository;

    }

    @PostMapping
    public ResponseEntity<ServerDTO> createServer(
            @RequestBody ServerDTO serverDTO,
            @AuthenticationPrincipal UserDetails userDetails) throws UserException {

        String email = userDetails.getUsername();

        User currentUser = userService.getUserEntityByEmail(email);

        ServerDTO createdServer = serverService.createServer(serverDTO, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdServer);
    }

    @GetMapping
    public ResponseEntity<List<ServerDTO>> getUserServers() throws ServerException, UserException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            throw new UserException("User not authenticated");
        }

        User currentUser = userService.getUserEntityByEmail(email);

        List<ServerDTO> servers = serverService.getServersByUser(currentUser);

        return ResponseEntity.ok(servers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getServer(@PathVariable Long id) throws ServerException {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateServer(@PathVariable Long id, @RequestBody ServerDTO serverDTO)
            throws ServerException, UserException {
        serverService.updateServer(id, serverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server updated");
    }

    @GetMapping("/servers/{serverId}/members")
    public ResponseEntity<List<ServerMemberDTO>> getServerMembers(
            @PathVariable Long serverId,
            Authentication authentication) throws ServerException, UserException {

        // 1️⃣ Log the serverId received
        System.out.println("Fetching members for serverId: " + serverId);

        // 2️⃣ Log the authenticated user
        String email = authentication.getName();
        System.out.println("Authenticated user email: " + email);
        User user = userService.getUserEntityByEmail(email);
        System.out.println("Authenticated user entity: " + user);

        // 3️⃣ Optional: log server existence
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> {
                    System.out.println("Server not found for id: " + serverId);
                    return new ServerException("Server not found");
                });
        System.out.println("Server found: " + server);

        // 4️⃣ If you add a membership check (forbidden if not member)
        boolean isMember = serverMemberRepository.existsByUserAndServer(user, server);
        System.out.println("Is user a member? " + isMember);
        if (!isMember) {
            System.out.println("User " + email + " is not a member of server " + serverId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this server");
        }

        // 5️⃣ Log members fetched
        List<ServerMemberDTO> members = serverMemberService.getMembersByServerId(serverId);
        System.out.println("Members fetched: " + members);

        return ResponseEntity.ok(members);
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteServer(@PathVariable Long id)
    //         throws ChannelException, ServerException, ServerMemberException {
    //     serverService.deleteServer(id);
    //     return ResponseEntity.status(HttpStatus.OK).body("Server deleted");
    // }

    // @PostMapping("/{id}/join")
    // public ResponseEntity<String> joinServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO)
    //         throws ServerMemberException {
    //     serverMemberService.createServerMember(id, userDTO.getId());
    //     return ResponseEntity.status(HttpStatus.CREATED).body("Server joined");
    // }

    // @DeleteMapping("/{id}/leave")
    // public ResponseEntity<String> leaveServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO)
    //         throws ServerException, ServerMemberException {
    //     Long userId = userDTO.getId();
    //     if (Objects.equals(serverService.getServerById(id).getOwnerId(), userId)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The owner cannot leave the server");
    //     }
    //     serverMemberService.deleteServerMember(id, userId);
    //     return ResponseEntity.status(HttpStatus.OK).body("Server left");
    // }

    // @GetMapping("/{id}/members")
    // public ResponseEntity<List<ServerMemberDTO>> getServerMembers(@PathVariable Long id) throws ServerMemberException {
    //     return ResponseEntity.ok(serverMemberService.getServerMembers(id));
    // }

    // @PutMapping("/{id}/members/{userId}")
    // public ResponseEntity<String> updateServerMember(@PathVariable Long id, @PathVariable Long userId,
    //         @RequestBody Long roleId) throws ServerMemberException {
    //     serverMemberService.updateServerMember(id, userId, roleId);
    //     return ResponseEntity.status(HttpStatus.CREATED).body("Server member updated");
    // }
}
/*
 * ✓ POST /servers - Create a new server
 * ✓ GET /servers - List user's servers
 * ✓ GET /server/{id} - Get server details
 * ✓ PUT /servers/{id} - Update server
 * ✓ DELETE /servers/{id} - Delete server
 * ✓ POST /servers/{id}/join - Join a server
 * ✓ DELETE /servers/{id}/leave - Leave a server
 * ✓ GET /servers/{id}/members - List server members
 * PUT /servers/{id}/members/:userId - Update member role
 */
