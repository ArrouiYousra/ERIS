package fourthargument.eris.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fourthargument.eris.api.dto.response.UserResponseDTO;
import fourthargument.eris.api.mapper.UserMapper;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.UserService;
import fourthargument.eris.exceptions.UserException;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) throws UserException {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);

        UserResponseDTO currentUser = userMapper.toDTO(user);

        return ResponseEntity.ok(currentUser);
    }

}