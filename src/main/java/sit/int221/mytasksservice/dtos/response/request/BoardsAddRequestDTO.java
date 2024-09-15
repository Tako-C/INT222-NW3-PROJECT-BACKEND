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
    @NotBlank
    @Size(min = 1, max = 50, message = "Board name must between 1 - 50")
    private String board_name;

}
