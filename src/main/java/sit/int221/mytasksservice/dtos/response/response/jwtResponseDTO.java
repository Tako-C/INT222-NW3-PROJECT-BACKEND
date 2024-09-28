package sit.int221.mytasksservice.dtos.response.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class jwtResponseDTO {
    private String access_token;
    private String refresh_token;
}
