package fourthargument.eris.api.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import fourthargument.eris.api.dto.request.SendPrivateMessageRequestDTO;
import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.PrivateMessageService;
import fourthargument.eris.api.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PrivateMessageWsController {

	private final PrivateMessageService privateMessageService;
	private final UserService userService;

	@MessageMapping("/private.send")
	public void sendPrivateMessage(
			@Payload SendPrivateMessageRequestDTO request,
			Principal principal) throws Exception {

		User sender = userService.getUserEntityByEmail(principal.getName());
		privateMessageService.sendPrivateMessage(
				request.getConversationId(),
				request.getContent(),
				sender);
		// Le service broadcast déjà sur /topic/conversation/{id}
	}
}