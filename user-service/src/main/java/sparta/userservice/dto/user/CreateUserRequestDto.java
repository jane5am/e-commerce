package sparta.userservice.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateUserRequestDto extends UserCommonDto {

    private String name;
    private String phone;
    private String address;

}