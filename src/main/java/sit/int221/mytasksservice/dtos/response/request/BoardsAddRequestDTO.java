package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardsAddRequestDTO {
    private String boardId;
    private String oid;
    @NotBlank
    @Size(min = 1, max = 120, message = "Board name must between 1 - 120")
    private String board_name;
    @Pattern(regexp = "^(private|public)$", message = "Visibility must be 'private' or 'public'")
    private String visibility;
}
