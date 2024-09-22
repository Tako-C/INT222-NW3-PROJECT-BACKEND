package sit.int221.mytasksservice.dtos.response.response;

public class ItemNotFoundException extends RuntimeException {
    public synchronized Throwable fillInStackThis() {
        return this;
    }
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException() {
        super("Not found");
    }
}
