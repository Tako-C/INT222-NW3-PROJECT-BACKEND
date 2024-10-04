package sit.int221.mytasksservice.dtos.response.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CollabResponseDTO {
    private Integer collabId;
    private String oid;
    private String name;
    private String email;
    private String accessRight;
    private String boardsId;
    private Timestamp added_on;
    private Timestamp updated_on;
}
