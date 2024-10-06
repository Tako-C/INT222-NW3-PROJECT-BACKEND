package sit.int221.mytasksservice.dtos.response.response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.services.TasksService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class AppErrorHandler extends Throwable {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ItemNotFoundException.class)
    public Map<String, Object> handleItemNotFoundException(ItemNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());
        response.put("instance", request.getRequestURI());
        return response;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = GeneralException.class)
    public static Map<String, Object> handleInternalServerValueError(HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
        response.put("instance", request.getRequestURI());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, Object> handleArgumentExceptions(BadRequestException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        return errors;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ResponseStatusException.class)
    public Map<String, Object> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("message", ex.getReason());
        response.put("instance", request.getRequestURI());
        return response;
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Map<String, Object> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", ex.getMessage());
        response.put("instance", request.getRequestURI());
        return response;
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateItemException.class)
    public Map<String, Object> handleConflictException(DuplicateItemException ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());
        response.put("instance", request.getRequestURI());
        return response;
    }

    @Autowired
    private TasksService tasksService;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String boardId = extractBoardIdFromUri(uri);

        if (boardId != null) {
            try {
                tasksService.checkBoardAccess(boardId);
            } catch (ItemNotFoundException e) {
                Map<String, Object> errors = new LinkedHashMap<>();
                errors.put("timestamp", LocalDateTime.now());
                errors.put("status", HttpStatus.NOT_FOUND.value());
                errors.put("message", "Board not found");
                return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
            } catch (ForbiddenException e) {
                Map<String, Object> errors = new LinkedHashMap<>();
                errors.put("timestamp", LocalDateTime.now());
                errors.put("status", HttpStatus.FORBIDDEN.value());
                errors.put("message", "Access Denied: You do not have permission to access this resource");
                return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
            }
        }
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", "Invalid or missing request body");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private String extractBoardIdFromUri(String uri) {
        String[] segments = uri.split("/");
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equals("boards") && (i + 1) < segments.length) {
                return segments[i + 1]; // คืนค่าบอร์ด ID
            }
        }
        return null;
    }

}