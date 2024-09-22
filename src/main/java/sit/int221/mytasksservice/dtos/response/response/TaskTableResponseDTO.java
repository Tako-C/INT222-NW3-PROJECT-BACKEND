package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.Statuses;

@Getter
@Setter
public class TaskTableResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private String statusName;
    private String boardName;
}
