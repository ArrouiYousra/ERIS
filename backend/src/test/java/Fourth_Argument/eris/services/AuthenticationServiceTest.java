package Fourth_Argument.eris.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import fourthargument.eris.api.dto.request.LoginRequestDTO;
import fourthargument.eris.api.dto.request.UserRequestDTO;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.api.services.AuthenticationService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRequestDTO signupDto;
    private LoginRequestDTO loginDto;
    private User existingUser;

    @BeforeEach
    void setUp() {
        signupDto = new UserRequestDTO();
        signupDto.setEmail("test@example.com");
        signupDto.setUsername("testuser");
        signupDto.setPassword("Password1");
        signupDto.setDisplayName("Test User");

        loginDto = new LoginRequestDTO();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password1");

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setUsername("testuser");
        existingUser.setPassword("encodedPassword");
        existingUser.setDisplayName("Test User");
    }

    // ── signup ──

    @Test
    void signup_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authenticationService.signup(signupDto);

        assertNotNull(result);
        // Note: User.getUsername() returns email (Spring Security override)
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_emailAlreadyUsed() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authenticationService.signup(signupDto));

        assertEquals("Email déjà utilisé", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_invalidPassword() {
        signupDto.setPassword("weak");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authenticationService.signup(signupDto));

        assertTrue(ex.getMessage().contains("mot de passe"));
        verify(userRepository, never()).save(any());
    }

    // ── login ──

    @Test
    void login_success() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Password1", "encodedPassword")).thenReturn(true);

        User result = authenticationService.login(loginDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authenticationService.login(loginDto));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void login_invalidPassword() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Password1", "encodedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authenticationService.login(loginDto));

        assertEquals("Invalid credentials", ex.getMessage());
    }
}
