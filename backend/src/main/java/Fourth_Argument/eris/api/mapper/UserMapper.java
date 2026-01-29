package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.UserDTO;
import Fourth_Argument.eris.api.model.User;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setDisplayName(user.getDisplayName());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDisplayName(dto.getDisplayName());
        return user;
    }
}