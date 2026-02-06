package Fourth_Argument.eris.api.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.ServerMemberDTO;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.model.User;
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
            UserService userService) {
        this.serverService = serverService;
        this.serverMemberService = serverMemberService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createServer(@RequestBody ServerDTO serverDTO) throws UserException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // usually the email
        } else {
            throw new RuntimeException("User not authenticated");
        }

        // 2. Fetch the actual entity from DB
        User currentUser = userService.getUserEntityByEmail(email);
        serverService.createServer(serverDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server created");
    }

    @GetMapping
    public ResponseEntity<List<ServerDTO>> getUserServers() throws UserException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            throw new RuntimeException("User not authenticated");
        }

        User currentUser = userService.getUserEntityByEmail(email);

        List<ServerDTO> servers = serverService.getUserServers(currentUser.getId());
        return ResponseEntity.ok(servers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateServer(@PathVariable Long id, @RequestBody ServerDTO serverDTO) {
        serverService.updateServer(id, serverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
        return ResponseEntity.status(HttpStatus.OK).body("Server deleted");
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO) {
        serverMemberService.createServerMember(id, userDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Server joined");
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<String> leaveServer(@PathVariable Long id, @RequestBody UserResponseDTO userDTO) {
        Long userId = userDTO.getId();
        if (Objects.equals(serverService.getServerById(id).getOwnerId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The owner cannot leave the server");
        }
        serverMemberService.deleteServerMember(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Server left");
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ServerMemberDTO>> getServerMembers(@PathVariable Long id) {
        return ResponseEntity.ok(serverMemberService.getServerMembers(id));
    }

    @PutMapping("/{id}/members/{userId}")
    public ResponseEntity<String> updateServerMember(@PathVariable Long id, @PathVariable Long userId,
            @RequestBody Long roleId) {
        serverMemberService.updateServerMember(id, userId, roleId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server member updated");
    }
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
