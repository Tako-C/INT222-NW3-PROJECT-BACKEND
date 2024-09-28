package sit.int221.mytasksservice.dtos.response.response;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;
import sit.int221.mytasksservice.models.secondary.Users;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
@Getter
@Setter
public class BoardDetailResponseDTO {
    private String boardId;
    private Owner owner;
    private String board_name;
    private String visibility;
    private List<TaskTableResponseDTO> tasks;
    private List<StatusTableResponseDTO> statuses;
    private Timestamp createdOn;
    private Timestamp updatedOn;
}
