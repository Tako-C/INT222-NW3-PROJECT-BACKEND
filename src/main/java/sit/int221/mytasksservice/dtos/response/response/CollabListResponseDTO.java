package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CollabListResponseDTO {
    private String boardName;
    private Owner owner;
    private List<CollabResponseDTO> collaborators;
}
