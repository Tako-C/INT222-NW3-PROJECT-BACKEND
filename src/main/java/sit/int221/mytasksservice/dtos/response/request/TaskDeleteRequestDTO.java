package sit.int221.mytasksservice.dtos.response.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDeleteRequestDTO {
    private Integer id;
    private String title;
    private String assignees;
    //    private TaskStatusEnum status;
    private String statusName;

}
