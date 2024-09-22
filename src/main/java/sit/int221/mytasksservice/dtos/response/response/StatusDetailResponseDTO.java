package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StatusDetailResponseDTO {
    private Integer statusId;
    private String name;
    private String description;
    private String boards;
}
