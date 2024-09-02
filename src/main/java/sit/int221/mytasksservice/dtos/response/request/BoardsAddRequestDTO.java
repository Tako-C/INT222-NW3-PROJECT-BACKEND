package sit.int221.mytasksservice.dtos.response.request;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;

import java.util.Set;
@Setter
@Getter
public class BoardsAddRequestDTO {
    private String boardId;
    private String oid;
    @NotBlank(message = "Board name is required")
    @Size(min = 1, max = 120, message = "Board name must be between 1 to 120.")
    private String board_name;

}
