package Fourth_Argument.eris.messagingstompwebsocket.user;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    public void saveUser(User user) {
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user) {
        var storeUser = userRepository.findById(user.getDisplayName())
                .orElse(null);
        if (storeUser != null) {
            storeUser.setStatus(UserStatus.OFFLINE);
            userRepository.save(storeUser);
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(UserStatus.ONLINE);
    }
}