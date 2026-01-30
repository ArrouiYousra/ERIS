package Fourth_Argument.eris.api.controllers;

import Fourth_Argument.eris.api.dto.ServerDTO;
import Fourth_Argument.eris.services.ServerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping
    public ResponseEntity<String> createServer(@RequestBody ServerDTO ServerDTO) {
        serverService.saveServer(ServerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Server created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerById(id));
    }
}
/*
 * ✓ POST /servers - Create a new server
 * GET /servers - List user's servers
 * ✓ GET /server/{id} - Get server details
 * PUT /servers/{id} - Update server
 * DELETE /servers/{id} - Delete server
 * POST /servers/{id}/join - Join a server
 * DELETE /servers/{id}/leave - Leave a server
 * GET /servers/{id}/members - List server members
 * PUT /servers/{id}/members/:userId - Update member role
 */
