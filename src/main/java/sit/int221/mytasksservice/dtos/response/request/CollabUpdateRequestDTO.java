package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CollabUpdateRequestDTO {
    @NotBlank
    @Pattern(regexp = "^(read|write)$", message = "Access right must be 'read' or 'write'")
    private String accessRight;
}