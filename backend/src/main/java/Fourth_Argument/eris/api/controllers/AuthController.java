package Fourth_Argument.eris.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fourth_Argument.eris.api.dto.request.LoginRequestDTO;
import Fourth_Argument.eris.api.dto.request.UserRequestDTO;
import Fourth_Argument.eris.api.dto.response.AuthResponseDTO;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.security.JwtUtil;
import Fourth_Argument.eris.services.UserService;
import lombok.RequiredArgsConstructor;
import Fourth_Argument.eris.api.model.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager; // vérifie les identifiants
    private final JwtUtil jwtUtil; // génère le token JWT
    private final UserService userService;

    @PostMapping("/signin")
    // Prend en argument une request http avec le body contenant l'email et le mot
    // de passe
    public ResponseEntity<String> signin(@RequestBody LoginRequestDTO dto) {
        // Création d'un token d'authentification en utilisant l'email et le mot de
        // passe en les vérifiant
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        // Vérification par Spring Security
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Génération du token JWT en utilisant l'email de l'utilisateur
        String token = jwtUtil.generateToken(userDetails.getUsername());
        // Retourne le token JWT dans la response http
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {

        UserResponseDTO userDetails = userService.createUser(userDTO);

        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity
                .ok(new AuthResponseDTO(token, userDetails));
    }
}
