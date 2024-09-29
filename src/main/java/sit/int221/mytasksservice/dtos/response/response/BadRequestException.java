package sit.int221.mytasksservice.dtos.response.response;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}