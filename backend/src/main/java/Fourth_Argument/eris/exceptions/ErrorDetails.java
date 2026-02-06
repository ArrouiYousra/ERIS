package Fourth_Argument.eris.exceptions;

import java.time.LocalDateTime;

public record ErrorDetails(String error, String message, LocalDateTime timeStamp) {
}