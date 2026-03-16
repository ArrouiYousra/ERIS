<<<<<<<< HEAD:backend/src/main/java/fourthargument/eris/api/mapper/UserMapper.java
package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.request.UserRequestDTO;
import fourthargument.eris.api.dto.response.UserResponseDTO;
import fourthargument.eris.api.model.User;
========
package fourth_argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourth_argument.eris.api.dto.request.UserRequestDTO;
import fourth_argument.eris.api.dto.response.UserResponseDTO;
import fourth_argument.eris.api.model.User;
>>>>>>>> origin/feature/CI-backend:backend/src/main/java/fourth_argument/eris/api/mapper/UserMapper.java

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUser());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        return dto;
    }

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDisplayName(dto.getDisplayName());
        return user;
    }
}