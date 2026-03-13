package fourth_argument.eris.api.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fourth_argument.eris.api.dto.request.LoginRequestDTO;
import fourth_argument.eris.api.dto.request.UserRequestDTO;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public final User signup(final UserRequestDTO input) {

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        if (!input.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new RuntimeException(
                    "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre");
        }

        User user = new User();

        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setDisplayName(input.getDisplayName());

        return userRepository.save(user);
    }

    public final User login(LoginRequestDTO input) {

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user == null) {
            throw new RuntimeException("Ce nom d'utilisateur n'existe pas !");
        }

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
