package Fourth_Argument.eris.api.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.request.LoginRequestDTO;
import Fourth_Argument.eris.api.dto.request.UserRequestDTO;
import Fourth_Argument.eris.api.dto.response.LoginResponseDTO;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.exceptions.UserException;
import Fourth_Argument.eris.services.AuthenticationService;
import Fourth_Argument.eris.services.JwtService;
import Fourth_Argument.eris.services.UserService;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService,
            UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody UserRequestDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginRequestDTO loginUserDto) {
        User authenticatedUser = authenticationService.login(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    // ── AJOUTÉ : récupérer le user connecté au reload ──
    @GetMapping("/me")
public ResponseEntity<Map<String, Object>> getMe(@AuthenticationPrincipal UserDetails userDetails) throws UserException {
        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        // Retourner un Map simple puisqu'il n'y a pas de UserResponseDTO
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "username", user.getUsername(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()));
    }
}