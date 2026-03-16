package fourthargument.eris.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import fourthargument.eris.api.dto.response.UserResponseDTO;
import fourthargument.eris.api.mapper.UserMapper;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.repository.UserRepository;
import fourthargument.eris.exceptions.UserException;

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
