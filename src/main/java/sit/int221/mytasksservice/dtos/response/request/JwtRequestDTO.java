package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JwtRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String username;

    @Size(min = 8,max = 14)
    @NotBlank
    private String password;

}
