package Fourth_Argument.eris.services;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.request.LoginRequestDTO;
import Fourth_Argument.eris.api.dto.request.UserRequestDTO;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.mapper.UserMapper;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }

    public User createUser(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        if (!dto.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new RuntimeException(
                    "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // À réecrire pour l'adapter au login auth mais c'est auth service alors

    // public UserResponseDTO loginUser(LoginRequestDTO dto) {

    // User user = userRepository.findByEmail(dto.getEmail())
    // .orElseThrow(() -> new RuntimeException("User not found"));

    // if (user == null) {
    // throw new RuntimeException("Ce nom d'utilisateur n'existe pas !");
    // }

    // if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
    // throw new RuntimeException("Invalid credentials");
    // }

    // return userMapper.toDTO(user);
    // }

}