package Fourth_Argument.eris.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import Fourth_Argument.eris.api.controllers.UserController;
import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.mapper.UserMapper;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController controller;

    private User user;
    private UserResponseDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setUsername("user");

        userDTO = new UserResponseDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
    }

    @Test
    void getCurrentUser_success() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        ResponseEntity<UserResponseDTO> response = controller.getCurrentUser(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user@example.com", response.getBody().getEmail());
    }

    @Test
    void getCurrentUser_authNull() throws Exception {
        ResponseEntity<UserResponseDTO> response = controller.getCurrentUser(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentUser_authNameNull() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(null);

        ResponseEntity<UserResponseDTO> response = controller.getCurrentUser(auth);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
