// package Fourth_Argument.eris.messagingstompwebsocket.user;

// import java.util.List;

// import org.springframework.stereotype.Service;

// import Fourth_Argument.eris.api.model.UserWs;
// import Fourth_Argument.eris.api.model.UserStatus;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class UserWsService {

// private final UserWsRepository userRepository;

// public void saveUser(UserWs user) {
// user.setStatus(UserStatus.ONLINE);
// userRepository.save(user);
// }

// public void disconnect(UserWs user) {
// var storeUser = userRepository.findById(user.getDisplayName())
// .orElse(null);
// if (storeUser != null) {
// storeUser.setStatus(UserStatus.OFFLINE);
// userRepository.save(storeUser);
// }
// }

// public List<UserWs> findConnectedUsers() {
// return userRepository.findAllByStatus(UserStatus.ONLINE);
// }
// }