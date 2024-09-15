package sit.int221.mytasksservice.dtos.response.response;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;

import java.util.Set;
@Getter
@Setter
public class BoardsResponseDTO {
    private String boardId;
    private String board_name;
}
