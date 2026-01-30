package Fourth_Argument.eris.messagingstompwebsocket.user;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWsService {

    private final UserWsRepository userRepository;

    public void saveUser(UserWs user) {
        user.setStatus(UserWsStatus.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(UserWs user) {
        var storeUser = userRepository.findById(user.getDisplayName())
                .orElse(null);
        if (storeUser != null) {
            storeUser.setStatus(UserWsStatus.OFFLINE);
            userRepository.save(storeUser);
        }
    }

    public List<UserWs> findConnectedUsers() {
        return userRepository.findAllByStatus(UserWsStatus.ONLINE);
    }
}