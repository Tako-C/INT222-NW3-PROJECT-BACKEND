package sit.int221.mytasksservice.dtos.response.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusDeleteRequestDTO {
    private Integer id;
    private String name;
    private String description;
}