package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollabUpdateRequestDTO {
    @NotBlank
    @Pattern(regexp = "^(read|write)$",
            message = "Access right must be 'read' or 'write'")
    private String accessRight;
}