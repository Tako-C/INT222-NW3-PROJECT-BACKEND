package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class BoardsResponseDTO {
    private String boardId;
    private Owner owner;
    private String board_name;
    private String visibility;
    private String accessRight;
    private Timestamp createdOn;
    private Timestamp updatedOn;
}
