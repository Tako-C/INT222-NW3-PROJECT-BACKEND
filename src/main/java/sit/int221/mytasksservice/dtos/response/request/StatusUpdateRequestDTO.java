package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@DynamicUpdate
public class StatusUpdateRequestDTO {
    private Integer statusId;
    @NotBlank(message = "Status name is required")
    @Size(min = 1, max = 50, message = "Status name must be between 1 and 50 characters")
    private String name;
    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;
    private String boards;

}
