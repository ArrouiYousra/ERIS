package Fourth_Argument.eris.api.controllers;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.api.dto.request.UpdateMemberRoleRequestDTO;
import Fourth_Argument.eris.api.dto.response.ServerMemberResponseDTO;
import Fourth_Argument.eris.services.ServerService;
import Fourth_Argument.eris.services.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    private final ServerService serverService;
    private final UserService userService;

    public ServerController(ServerService serverService, UserService userService) {
        this.serverService = serverService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        return userService.getUserByEmail(auth.getName()).getId();
    }

    @PostMapping
    public ResponseEntity<String> createServer(@RequestBody ServerDTO ServerDTO) {
        serverService.createServer(ServerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server created");
    }

    @GetMapping
    public ResponseEntity<List<ServerDTO>> getServers() {
        return ResponseEntity.ok(serverService.getServers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateServer(@PathVariable Long id, @RequestBody ServerDTO ServerDTO) {
        serverService.updateServer(id, ServerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
        return ResponseEntity.status(HttpStatus.OK).body("Server deleted");
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinServer(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        serverService.joinServer(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Joined server");
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<String> leaveServer(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        serverService.leaveServer(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Left server");
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ServerMemberResponseDTO>> getServerMembers(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerMembers(id));
    }

    @PutMapping("/{id}/members/{userId}")
    public ResponseEntity<String> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestBody UpdateMemberRoleRequestDTO body,
            Authentication authentication) {
        if (body == null || body.getRoleId() == null) {
            return ResponseEntity.badRequest().body("roleId required");
        }
        serverService.updateMemberRole(id, userId, body.getRoleId());
        return ResponseEntity.ok("Member role updated");
    }
}
/*
 * ✓ POST /servers - Create a new server
 * ✓? GET /servers - List user's servers
 * ✓ GET /server/{id} - Get server details
 * ✓ PUT /servers/{id} - Update server
 * ✓ DELETE /servers/{id} - Delete server
 * POST /servers/{id}/join - Join a server
 * DELETE /servers/{id}/leave - Leave a server
 * GET /servers/{id}/members - List server members
 * PUT /servers/{id}/members/:userId - Update member role
 */
