package sit.int221.mytasksservice.dtos.response.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception สำหรับการเกิดข้อผิดพลาดในการส่งอีเมล
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
