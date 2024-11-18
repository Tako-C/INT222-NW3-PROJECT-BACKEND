package sit.int221.mytasksservice.dtos.response.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDTO {
    private int status;
    private String message;
    private String instance;
    private CollabResponseDTO user;
}
