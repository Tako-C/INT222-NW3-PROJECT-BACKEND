package sit.int221.mytasksservice.dtos.response.response;

public class GeneralException extends RuntimeException {
    public synchronized Throwable fillInStackThis() {
        return this;
    }
}
