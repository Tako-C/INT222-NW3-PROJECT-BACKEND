package sit.int221.mytasksservice.dtos.response.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GeneralException extends RuntimeException {
    public synchronized Throwable fillInStackThis() {
        return this;
    }
}
