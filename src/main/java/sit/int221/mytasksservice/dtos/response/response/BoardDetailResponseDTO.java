package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

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
