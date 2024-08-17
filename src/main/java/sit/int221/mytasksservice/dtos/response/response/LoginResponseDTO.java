package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String username;
    private String encodedPassword;
}
