package fourthargument.eris.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrivateMessageEvent {
	private String type; // "NEW" | "EDIT" | "DELETE"
	private Object data;
}