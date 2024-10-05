package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CollabAddRequestDTO {

    @NotBlank
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.(co|th|com)$",
            message = "Email must be in the format: name@domain.co.th, name@domain.com, etc.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(read|write)$", message = "Access right must be 'read' or 'write'")
    private String accessRight;
}
