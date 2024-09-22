package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAddRequestDTO {

    private Integer id;

    @NotBlank(message = "Title is required")
    @Size(max=100)
    private String title;

    @Size(min=1, max=500)
    private String description;

    @Size(min=1, max=30)
    private String assignees;

    private String status;
    private String boards;
}

