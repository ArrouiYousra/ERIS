package fourthargument.eris.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

class GlobalExceptionsTest {

    private GlobalExceptions handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptions();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void channelExceptionHandler() {
        ChannelException ex = new ChannelException("Channel error");
        ResponseEntity<ErrorDetails> response = handler.channelExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Channel error", response.getBody().message());
        assertEquals("uri=/test", response.getBody().error());
    }

    @Test
    void roleExceptionHandler() {
        RoleException ex = new RoleException("Role error");
        ResponseEntity<ErrorDetails> response = handler.channelExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role error", response.getBody().message());
    }

    @Test
    void messageExceptionHandler() {
        MessageException ex = new MessageException("Message error");
        ResponseEntity<ErrorDetails> response = handler.messageExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Message error", response.getBody().message());
    }

    @Test
    void serverExceptionHandler() {
        ServerException ex = new ServerException("Server error");
        ResponseEntity<ErrorDetails> response = handler.serverExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Server error", response.getBody().message());
    }

    @Test
    void serverMemberExceptionHandler() {
        ServerMemberException ex = new ServerMemberException("Member error");
        ResponseEntity<ErrorDetails> response = handler.serverMemberExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Member error", response.getBody().message());
    }

    @Test
    void userExceptionHandler() {
        UserException ex = new UserException("User error");
        ResponseEntity<ErrorDetails> response = handler.userExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User error", response.getBody().message());
    }

    @Test
    void noHandlerFoundExceptionHandler() {
        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getMessage()).thenReturn("No handler found");

        ResponseEntity<ErrorDetails> response = handler.noHandlerFoundExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No handler found", response.getBody().message());
    }

    @Test
    void genericExceptionHandler() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ErrorDetails> response = handler.exceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    void methodArgumentNotValidExceptionHandler() {
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        ResponseEntity<ErrorDetails> response = handler.methodArgumentNotValidExceptionHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be null", response.getBody().message());
    }
}
