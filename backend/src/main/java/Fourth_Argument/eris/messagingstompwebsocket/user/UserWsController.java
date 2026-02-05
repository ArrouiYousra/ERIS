// package Fourth_Argument.eris.messagingstompwebsocket.user;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;

// import Fourth_Argument.eris.api.model.UserWs;
// import lombok.RequiredArgsConstructor;

// @Controller
// @RequiredArgsConstructor

// public class UserWsController {
// private final UserWsService service;

// @MessageMapping("/user.addUser")
// @SendTo("/user/topic")
// public UserWs addUser(
// @Payload UserWs user) {
// service.saveUser(user);
// return user;
// }

// @MessageMapping("/user.disconnectUser")
// @SendTo("/user/topic")
// public UserWs disconnect(
// @Payload UserWs user) {
// service.disconnect(user);
// return user;
// }

// @GetMapping("/user")
// public ResponseEntity<List<UserWs>> findConnectedUsers() {
// return ResponseEntity.ok(service.findConnectedUsers());
// }
// }
