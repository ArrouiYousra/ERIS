package fourthargument.eris.controllers;

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

import fourthargument.eris.api.controllers.AuthenticationController;
import fourthargument.eris.api.dto.request.LoginRequestDTO;
import fourthargument.eris.api.dto.request.UserRequestDTO;
import fourthargument.eris.api.dto.response.LoginResponseDTO;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.AuthenticationService;
import fourthargument.eris.api.services.JwtService;
import fourthargument.eris.api.services.UserService;

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

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("username");
        user.setDisplayName("alex");
        when(userService.getUserEntityByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<fourthargument.eris.api.dto.response.UserResponseDTO> response = controller.getMe(userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("alex", response.getBody().getDisplayName());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void getMe_displayNameNull_fallsBackToUsername() throws Exception {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setDisplayName(null);
        when(userService.getUserEntityByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<fourthargument.eris.api.dto.response.UserResponseDTO> response = controller.getMe(userDetails);

        fourthargument.eris.api.dto.response.UserResponseDTO body = response.getBody();
        assertNotNull(body, "Le body ne devrait pas être nul !");
        assertEquals("test@example.com", body.getEmail());
        assertEquals("testuser", body.getDisplayName());
    }
}
