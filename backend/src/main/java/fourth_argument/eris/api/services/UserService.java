package fourth_argument.eris.api.services;

import org.springframework.stereotype.Service;

import fourth_argument.eris.api.dto.response.UserResponseDTO;
import fourth_argument.eris.api.mapper.UserMapper;
import fourth_argument.eris.api.model.User;
import fourth_argument.eris.api.repository.UserRepository;
import fourth_argument.eris.exceptions.*;
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
