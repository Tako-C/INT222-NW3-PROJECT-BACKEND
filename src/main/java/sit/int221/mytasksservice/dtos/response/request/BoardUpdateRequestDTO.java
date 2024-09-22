package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardUpdateRequestDTO {
    @Pattern(regexp = "^(private|public)$", message = "Visibility must be 'private' or 'public'")
    private String visibility;
}