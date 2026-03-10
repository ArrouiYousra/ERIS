package Fourth_Argument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import Fourth_Argument.eris.api.controllers.AuthenticationController;
import Fourth_Argument.eris.api.dto.request.LoginRequestDTO;
import Fourth_Argument.eris.api.dto.request.UserRequestDTO;
import Fourth_Argument.eris.api.dto.response.LoginResponseDTO;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.services.AuthenticationService;
import Fourth_Argument.eris.api.services.JwtService;
import Fourth_Argument.eris.api.services.UserService;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController controller;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setDisplayName("Test User");
        testUser.setPassword("encoded");
    }

    @Test
    void register_success() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("Password1");
        dto.setUsername("testuser");

        when(authenticationService.signup(dto)).thenReturn(testUser);

        ResponseEntity<User> response = controller.register(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void authenticate_success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("Password1");

        when(authenticationService.login(dto)).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<LoginResponseDTO> response = controller.authenticate(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals(3600000L, response.getBody().getExpiresIn());
    }

    @Test
    void getMe_success() throws Exception {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.getUserEntityByEmail("test@example.com")).thenReturn(testUser);

        ResponseEntity<UserResponseDTO> response = controller.getMe(userDetails);

        assertEquals(200, response.getStatusCode().value());
        UserResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
        assertEquals("test@example.com", body.getEmail());
    }

    @Test
    void getMe_displayNameNull_fallsBackToUsername() throws Exception {
        testUser.setDisplayName(null);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.getUserEntityByEmail("test@example.com")).thenReturn(testUser);

        ResponseEntity<UserResponseDTO> response = controller.getMe(userDetails);

        UserResponseDTO body = response.getBody();
        // User.getUsername() returns email, so fallback is email
        assertEquals("testuser", body.getEmail());
    }
}
