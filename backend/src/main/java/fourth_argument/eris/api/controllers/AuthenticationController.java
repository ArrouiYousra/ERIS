package fourth_argument.eris.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourth_argument.eris.api.dto.request.LoginRequestDTO;
import fourth_argument.eris.api.dto.request.UserRequestDTO;
import fourth_argument.eris.api.dto.response.LoginResponseDTO;
import fourth_argument.eris.api.dto.response.UserResponseDTO;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.services.AuthenticationService;
import fourth_argument.eris.api.services.JwtService;
import fourth_argument.eris.api.services.UserService;
import fourth_argument.eris.exceptions.UserException;

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
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal UserDetails userDetails)
            throws UserException {
        UserResponseDTO user = userService.getUserByEmail(userDetails.getUsername());
        // Retourner un Map simple puisqu'il n'y a pas de UserResponseDTO
        return ResponseEntity.ok(user);
    }
}