package fourthargument.eris.exceptions;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(ChannelException.class)
    public ResponseEntity<ErrorDetails> channelExceptionHandler(ChannelException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleException.class)
    public ResponseEntity<ErrorDetails> channelExceptionHandler(RoleException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetails> messageExceptionHandler(MessageException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorDetails> serverExceptionHandler(ServerException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerMemberException.class)
    public ResponseEntity<ErrorDetails> serverMemberExceptionHandler(ServerMemberException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversationException.class)
    public ResponseEntity<ErrorDetails> conversationExceptionHandler(ConversationException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PrivateMessageException.class)
    public ResponseEntity<ErrorDetails> privateMessageExceptionHandler(PrivateMessageException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e,
            WebRequest request) {
        String err = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorDetails error = new ErrorDetails(e.getMessage(), err,
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetails> noHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest request) {
        ErrorDetails error = new ErrorDetails("No handler available for this endpoint", e.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> exceptionHandler(Exception e, WebRequest request) {
        ErrorDetails error = new ErrorDetails(request.getDescription(false), e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}