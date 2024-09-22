package sit.int221.mytasksservice.dtos.response.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;

@Getter
@Setter
public class TaskDetailResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private String statusName;
    private String boardName;
    private Timestamp createdOn;
    private Timestamp updatedOn;
}

