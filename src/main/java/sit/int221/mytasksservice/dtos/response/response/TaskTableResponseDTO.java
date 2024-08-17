package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTableResponseDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String statusName;
}
