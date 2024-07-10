package sparta.userservice.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SendEmailRequestDto {

    private int userId;

    @Email
    private String email;
}
