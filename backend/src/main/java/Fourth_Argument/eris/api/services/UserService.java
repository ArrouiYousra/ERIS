package Fourth_Argument.eris.api.services;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.api.dto.response.UserResponseDTO;
import Fourth_Argument.eris.api.mapper.UserMapper;
import Fourth_Argument.eris.api.model.User;
import Fourth_Argument.eris.api.repository.UserRepository;
import Fourth_Argument.eris.exceptions.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO getUserById(Long id) throws UserException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));
        return userMapper.toDTO(user);
    }

    public User getUserEntityByEmail(String email) throws UserException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

    }

    public UserResponseDTO getUserByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

        return userMapper.toDTO(user);
    }

}
