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
    @Size(max = 50, message = "Username or password is invalid")
    private String userName;
    @NotBlank
    @Size(min = 8,max = 14, message = "Username or password is invalid")
    private String password;
}
