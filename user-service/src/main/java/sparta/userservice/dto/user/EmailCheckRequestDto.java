package sparta.userservice.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailCheckRequestDto {

    private int userId;

    @Email
    private String email;

    private String certificationNumber;
}
