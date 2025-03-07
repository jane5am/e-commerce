package sparta.userservice.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWishListRequestDto {
    private int productId;
    private int quantity;
}
