package sit.int221.mytasksservice.dtos.response.response;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
