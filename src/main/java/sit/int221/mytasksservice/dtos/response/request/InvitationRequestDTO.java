package sit.int221.mytasksservice.dtos.response.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequestDTO {

    @NotBlank(message = "Invitee email is required")
    @Email(message = "Invalid email format")
    private String inviteeEmail;

    @NotBlank(message = "Access right is required")
    private String accessRight;
}
